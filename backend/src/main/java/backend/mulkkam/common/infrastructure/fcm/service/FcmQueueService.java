package backend.mulkkam.common.infrastructure.fcm.service;

import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.common.infrastructure.fcm.domain.NotificationOutbox;
import backend.mulkkam.common.infrastructure.fcm.domain.NotificationOutboxStatus;
import backend.mulkkam.common.infrastructure.fcm.domain.NotificationOutboxTargetType;
import backend.mulkkam.common.infrastructure.fcm.dto.request.SendMessageByFcmTokenRequest;
import backend.mulkkam.common.infrastructure.fcm.dto.request.SendMessageByFcmTokensRequest;
import backend.mulkkam.common.infrastructure.fcm.dto.request.SendMessageByFcmTopicRequest;
import backend.mulkkam.common.infrastructure.fcm.repository.NotificationOutboxRepository;
import backend.mulkkam.common.exception.errorCode.FirebaseErrorCode;
import backend.mulkkam.device.repository.DeviceRepository;
import com.google.common.collect.Lists;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class FcmQueueService {

    private static final int FCM_BATCH_SIZE = 500;

    private final NotificationOutboxRepository notificationOutboxRepository;
    private final DeviceRepository deviceRepository;
    private final FcmClient fcmClient;
    private final int batchSize;
    private final int maxAttempts;
    private final int baseBackoffSeconds;

    public FcmQueueService(
            NotificationOutboxRepository notificationOutboxRepository,
            DeviceRepository deviceRepository,
            FcmClient fcmClient,
            @Value("${fcm.queue.batch-size:100}") int batchSize,
            @Value("${fcm.queue.max-attempts:4}") int maxAttempts,
            @Value("${fcm.queue.base-backoff-seconds:1}") int baseBackoffSeconds
    ) {
        this.notificationOutboxRepository = notificationOutboxRepository;
        this.deviceRepository = deviceRepository;
        this.fcmClient = fcmClient;
        this.batchSize = batchSize;
        this.maxAttempts = maxAttempts;
        this.baseBackoffSeconds = baseBackoffSeconds;
    }

    @Transactional
    public void enqueueTopic(SendMessageByFcmTopicRequest request) {
        NotificationOutbox outbox = NotificationOutbox.forTopic(
                request.title(),
                request.body(),
                request.topic(),
                request.action(),
                maxAttempts,
                LocalDateTime.now()
        );
        notificationOutboxRepository.save(outbox);
    }

    @Transactional
    public void enqueueToken(SendMessageByFcmTokenRequest request) {
        NotificationOutbox outbox = NotificationOutbox.forToken(
                request.title(),
                request.body(),
                request.token(),
                request.action(),
                maxAttempts,
                LocalDateTime.now()
        );
        notificationOutboxRepository.save(outbox);
    }

    @Transactional
    public void enqueueTokens(SendMessageByFcmTokensRequest request) {
        if (request.allTokens().isEmpty()) {
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        Lists.partition(request.allTokens(), FCM_BATCH_SIZE)
                .forEach(tokens -> {
                    List<NotificationOutbox> outboxes = tokens.stream()
                            .map(token -> NotificationOutbox.forToken(
                                    request.title(),
                                    request.body(),
                                    token,
                                    request.action(),
                                    maxAttempts,
                                    now
                            ))
                            .toList();
                    notificationOutboxRepository.saveAll(outboxes);
                });
    }

    @Transactional
    public void processPendingQueue() {
        LocalDateTime now = LocalDateTime.now();
        List<NotificationOutbox> pendingQueues = notificationOutboxRepository.findPendingForUpdate(
                NotificationOutboxStatus.PENDING.name(),
                now,
                batchSize
        );

        for (NotificationOutbox outbox : pendingQueues) {
            try {
                send(outbox);
                outbox.markSuccess(now);
            } catch (Exception e) {
                handleFailure(outbox, now, e);
            }
        }
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

    private void handleFailure(NotificationOutbox outbox, LocalDateTime now, Exception e) {
        String errorCode = resolveErrorCode(e);
        String errorMessage = resolveErrorMessage(e);
        if (e instanceof CommonException commonException
                && commonException.getErrorCode() instanceof FirebaseErrorCode firebaseErrorCode) {
            if (shouldRemoveToken(firebaseErrorCode)) {
                removeTokenIfNeeded(outbox);
                outbox.markFailedNow(now, errorCode, errorMessage);
                log.warn("[FCM QUEUE DROPPED TOKEN] id={}, errorCode={}", outbox.getId(), errorCode);
                return;
            }
            if (shouldFailFast(firebaseErrorCode)) {
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

    private boolean shouldRemoveToken(FirebaseErrorCode firebaseErrorCode) {
        return firebaseErrorCode == FirebaseErrorCode.INVALID_ARGUMENT
                || firebaseErrorCode == FirebaseErrorCode.UNREGISTERED;
    }

    private boolean shouldFailFast(FirebaseErrorCode firebaseErrorCode) {
        return firebaseErrorCode == FirebaseErrorCode.SENDER_ID_MISMATCH
                || firebaseErrorCode == FirebaseErrorCode.THIRD_PARTY_AUTH_ERROR;
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
