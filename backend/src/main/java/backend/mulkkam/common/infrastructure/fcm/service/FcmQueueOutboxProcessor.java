package backend.mulkkam.common.infrastructure.fcm.service;

import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.common.exception.errorCode.FirebaseErrorCode;
import backend.mulkkam.common.infrastructure.fcm.domain.FcmErrorHandlingStrategy;
import backend.mulkkam.common.infrastructure.fcm.domain.NotificationOutbox;
import backend.mulkkam.common.infrastructure.fcm.domain.NotificationOutboxStatus;
import backend.mulkkam.common.infrastructure.fcm.domain.NotificationOutboxTargetType;
import backend.mulkkam.common.infrastructure.fcm.dto.request.SendMessageByFcmTokenRequest;
import backend.mulkkam.common.infrastructure.fcm.dto.request.SendMessageByFcmTopicRequest;
import backend.mulkkam.common.infrastructure.fcm.repository.NotificationOutboxRepository;
import backend.mulkkam.device.repository.DeviceRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class FcmQueueOutboxProcessor {

    private final NotificationOutboxRepository notificationOutboxRepository;
    private final DeviceRepository deviceRepository;
    private final FcmClient fcmClient;
    private final int baseBackoffSeconds;

    public FcmQueueOutboxProcessor(
            NotificationOutboxRepository notificationOutboxRepository,
            DeviceRepository deviceRepository,
            FcmClient fcmClient,
            @Value("${fcm.queue.base-backoff-seconds:1}") int baseBackoffSeconds
    ) {
        this.notificationOutboxRepository = notificationOutboxRepository;
        this.deviceRepository = deviceRepository;
        this.fcmClient = fcmClient;
        this.baseBackoffSeconds = baseBackoffSeconds;
    }

    @Transactional
    public boolean processNext(LocalDateTime now) {
        List<NotificationOutbox> pendingQueues = notificationOutboxRepository.findPendingForUpdate(
                NotificationOutboxStatus.PENDING.name(),
                now,
                1
        );
        if (pendingQueues.isEmpty()) {
            return false;
        }
        NotificationOutbox outbox = pendingQueues.getFirst();
        try {
            send(outbox);
            outbox.markSuccess(now);
        } catch (Exception e) {
            handleFailure(outbox, now, e);
        }
        return true;
    }

    private void send(NotificationOutbox outbox) {
        switch (outbox.getTargetType()) {
            case TOPIC -> fcmClient.sendMessageByTopic(new SendMessageByFcmTopicRequest(
                    outbox.getTitle(),
                    outbox.getBody(),
                    outbox.getTopic(),
                    outbox.getAction()
            ));
            case TOKEN -> fcmClient.sendMessageByToken(new SendMessageByFcmTokenRequest(
                    outbox.getTitle(),
                    outbox.getBody(),
                    outbox.getToken(),
                    outbox.getAction()
            ));
        }
    }

    private void handleFailure(NotificationOutbox outbox, LocalDateTime now, Exception e) {
        String errorCode = resolveErrorCode(e);
        String errorMessage = resolveErrorMessage(e);
        if (e instanceof CommonException commonException
                && commonException.getErrorCode() instanceof FirebaseErrorCode firebaseErrorCode) {
            FcmErrorHandlingStrategy strategy = FcmErrorHandlingStrategy.from(firebaseErrorCode);
            if (strategy.shouldRemoveToken()) {
                removeTokenIfNeeded(outbox);
            }
            if (!strategy.isRetryable() || strategy.shouldFailFast()) {
                outbox.markFailedNow(now, errorCode, errorMessage);
                log.warn("[FCM QUEUE FAILED FAST] id={}, errorCode={}", outbox.getId(), errorCode);
                return;
            }
        }

        outbox.markFailure(now, errorCode, errorMessage, baseBackoffSeconds);
        log.warn("[FCM QUEUE FAILED] id={}, status={}, errorCode={}",
                outbox.getId(),
                outbox.getStatus(),
                errorCode);
    }

    private String resolveErrorCode(Exception e) {
        if (e instanceof CommonException commonException) {
            return commonException.getErrorCode().name();
        }
        return e.getClass().getSimpleName();
    }

    private String resolveErrorMessage(Exception e) {
        String message = e.getMessage();
        if (message == null || message.isBlank()) {
            return e.getClass().getName();
        }
        return message;
    }

    private void removeTokenIfNeeded(NotificationOutbox outbox) {
        if (outbox.getTargetType() != NotificationOutboxTargetType.TOKEN) {
            return;
        }
        String token = outbox.getToken();
        if (token == null || token.isBlank()) {
            return;
        }
        deviceRepository.deleteByToken(token);
    }
}
