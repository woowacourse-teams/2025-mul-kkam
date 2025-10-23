package backend.mulkkam.common.infrastructure.fcm.service;

import backend.mulkkam.common.infrastructure.fcm.dto.request.SendMessageByFcmTokenRequest;
import backend.mulkkam.common.infrastructure.fcm.dto.request.SendMessageByFcmTokensRequest;
import backend.mulkkam.common.infrastructure.fcm.dto.request.SendMessageByFcmTopicRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Async("producerExecutor")
@RequiredArgsConstructor
@Component
public class NotificationEventHandler {

    private final NotificationMQProducer producer;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onTopic(SendMessageByFcmTopicRequest e) {
        producer.publishTopic(e);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onToken(SendMessageByFcmTokenRequest e) {
        producer.publishToken(e);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onTokens(SendMessageByFcmTokensRequest e) {
        producer.publishTokens(e);
    }
}
