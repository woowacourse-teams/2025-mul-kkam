package backend.mulkkam.common.infrastructure.fcm.service;

import backend.mulkkam.common.infrastructure.fcm.domain.NotificationOutbox;
import backend.mulkkam.common.infrastructure.fcm.dto.request.SendMessageByFcmTokenRequest;
import backend.mulkkam.common.infrastructure.fcm.dto.request.SendMessageByFcmTokensRequest;
import backend.mulkkam.common.infrastructure.fcm.dto.request.SendMessageByFcmTopicRequest;
import backend.mulkkam.common.infrastructure.fcm.repository.NotificationOutboxRepository;
import com.google.common.collect.Lists;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FcmQueueService {

    private static final int FCM_BATCH_SIZE = 500;

    private final NotificationOutboxRepository notificationOutboxRepository;
    private final FcmQueueOutboxProcessor fcmQueueOutboxProcessor;
    private final int batchSize;
    private final int maxAttempts;

    public FcmQueueService(
            NotificationOutboxRepository notificationOutboxRepository,
            FcmQueueOutboxProcessor fcmQueueOutboxProcessor,
            @Value("${fcm.queue.batch-size:100}") int batchSize,
            @Value("${fcm.queue.max-attempts:4}") int maxAttempts
    ) {
        this.notificationOutboxRepository = notificationOutboxRepository;
        this.fcmQueueOutboxProcessor = fcmQueueOutboxProcessor;
        this.batchSize = batchSize;
        this.maxAttempts = maxAttempts;
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

    public void processPendingQueue() {
        if (batchSize < 1) {
            return;
        }
        for (int i = 0; i < batchSize; i++) {
            boolean processed = fcmQueueOutboxProcessor.processNext(LocalDateTime.now());
            if (!processed) {
                break;
            }
        }
    }
}
