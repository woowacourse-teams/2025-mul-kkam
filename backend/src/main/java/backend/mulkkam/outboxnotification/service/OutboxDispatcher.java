package backend.mulkkam.outboxnotification.service;

import static backend.mulkkam.common.exception.errorCode.FirebaseErrorCode.isPermanentError;
import static backend.mulkkam.common.exception.errorCode.InternalServerErrorErrorCode.INTER_SERVER_ERROR_CODE;

import backend.mulkkam.common.exception.AlarmException;
import backend.mulkkam.common.exception.errorCode.ErrorCode;
import backend.mulkkam.common.exception.errorCode.FirebaseErrorCode;
import backend.mulkkam.common.infrastructure.fcm.dto.request.SendMessageByFcmTokenRequest;
import backend.mulkkam.common.infrastructure.fcm.service.FcmClient;
import backend.mulkkam.notification.dto.NotificationMessageTemplate;
import backend.mulkkam.outboxnotification.domain.OutboxNotification;
import backend.mulkkam.outboxnotification.repository.OutboxNotificationRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OutboxDispatcher {

    private static final int MAX_RETRY_COUNT = 3;
    private static final int BATCH_SIZE = 500;
    private static final int DISPATCH_INTERVAL_MS = 2000;

    private final OutboxNotificationRepository outboxRepository;
    private final FcmClient fcmClient;

    // 외부 API와 함께 동기로 동작합니다.
    // 이후 병목 지점이 생긴다면 외부 API 호출 (FCM), 응답 파싱 및 상태 변경을 비동기로 변경합니다. #1029
    @Scheduled(fixedDelay = DISPATCH_INTERVAL_MS)
    @Transactional
    public void dispatch() {
        List<OutboxNotification> jobs = outboxRepository.fetchReadyForSend(BATCH_SIZE);
        if (jobs.isEmpty()) {
            return;
        }
        for (OutboxNotification job : jobs) {
            outboxRepository.markSending(job.getId());
            NotificationMessageTemplate notificationMessageTemplate = new NotificationMessageTemplate(job.getTitle(),
                    job.getBody(), job.getAction(), job.getType());
            try {
                fcmClient.sendMessageByToken(
                        new SendMessageByFcmTokenRequest(notificationMessageTemplate, job.getToken()));
                outboxRepository.markSent(job.getId());
            } catch (Exception exception) {
                handleFailure(job, exception);
            }
        }
    }

    private void handleFailure(
            OutboxNotification job,
            Exception exception
    ) {
        FirebaseErrorCode firebaseErrorCode = (FirebaseErrorCode) extractErrorCode(exception);
        if (isPermanentError(firebaseErrorCode)) {
            outboxRepository.markFail(job.getId(), firebaseErrorCode.name());
            return;
        }
        outboxRepository.markRetryOrFail(
                job.getId(),
                nextBackoffTime(job.getAttemptCount()),
                firebaseErrorCode.name(),
                MAX_RETRY_COUNT
        );
    }

    private ErrorCode extractErrorCode(Exception exception) {
        if (exception instanceof AlarmException alarmException) {
            if (alarmException.getErrorCode() != null) {
                return alarmException.getErrorCode();
            }
        }
        return INTER_SERVER_ERROR_CODE;
    }

    private LocalDateTime nextBackoffTime(int attempt) {
        long sec = Math.min(60, (long) (2 * Math.pow(2, attempt)));
        return LocalDateTime.now().plusSeconds(sec);
    }
}
