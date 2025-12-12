package backend.mulkkam.outboxnotification.service;

import static backend.mulkkam.common.exception.errorCode.FirebaseErrorCode.isPermanentError;

import backend.mulkkam.common.exception.AlarmException;
import backend.mulkkam.common.exception.errorCode.FirebaseErrorCode;
import backend.mulkkam.common.infrastructure.fcm.dto.request.SendMessageByFcmTokenRequest;
import backend.mulkkam.common.infrastructure.fcm.service.FcmClient;
import backend.mulkkam.notification.dto.NotificationMessageTemplate;
import backend.mulkkam.outboxnotification.domain.OutboxNotification;
import backend.mulkkam.outboxnotification.repository.OutboxNotificationRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class OutboxProcessor {

    private static final int MAX_RETRY_COUNT = 3;
    private static final int BATCH_SIZE = 500;

    private final FcmClient fcmClient;
    private final OutboxNotificationRepository outboxNotificationRepository;

    // 외부 API(알림 FCM)와 함께 동기로 동작합니다.
    // 병목 지점이 생긴다면 외부 API 호출 (FCM), 응답 파싱 및 상태 변경을 비동기로 변경합니다. #1029
    @Transactional
    public void process() {
        List<OutboxNotification> jobs = outboxNotificationRepository.fetchReadyForSend(BATCH_SIZE);
        if (jobs.isEmpty()) {
            return;
        }
        for (OutboxNotification job : jobs) {
            outboxNotificationRepository.markSending(job.getId());
            NotificationMessageTemplate notificationMessageTemplate = new NotificationMessageTemplate(job.getTitle(),
                    job.getBody(), job.getAction(), job.getType());
            try {
                fcmClient.sendMessageByToken(
                        new SendMessageByFcmTokenRequest(notificationMessageTemplate, job.getToken()));
                outboxNotificationRepository.markSent(job.getId());
            } catch (AlarmException alarmException) {
                handleFailure(job, alarmException);
            }
        }
    }

    private void handleFailure(
            OutboxNotification job,
            AlarmException alarmException
    ) {
        FirebaseErrorCode firebaseErrorCode = extractErrorCode(alarmException);
        if (isPermanentError(firebaseErrorCode)) {
            outboxNotificationRepository.markFail(job.getId(), firebaseErrorCode.name());
            return;
        }
        outboxNotificationRepository.markRetryOrFail(
                job.getId(),
                nextBackoffTime(job.getAttemptCount()),
                firebaseErrorCode.name(),
                MAX_RETRY_COUNT
        );
    }

    private FirebaseErrorCode extractErrorCode(AlarmException alarmException) {
        if (alarmException.getErrorCode() != null) {
            return FirebaseErrorCode.findByName(alarmException.getErrorCode().name());
        }
        return FirebaseErrorCode.INTERNAL;
    }

    private LocalDateTime nextBackoffTime(int attempt) {
        long sec = Math.min(60, (long) (2 * Math.pow(2, attempt)));
        return LocalDateTime.now().plusSeconds(sec);
    }
}
