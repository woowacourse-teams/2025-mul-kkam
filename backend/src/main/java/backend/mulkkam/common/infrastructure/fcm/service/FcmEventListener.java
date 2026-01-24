package backend.mulkkam.common.infrastructure.fcm.service;

import backend.mulkkam.common.infrastructure.fcm.dto.request.SendMessageByFcmTokenRequest;
import backend.mulkkam.common.infrastructure.fcm.dto.request.SendMessageByFcmTokensRequest;
import backend.mulkkam.common.infrastructure.fcm.dto.request.SendMessageByFcmTopicRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Async
@RequiredArgsConstructor
@Component
public class FcmEventListener {

    private final FcmQueueService fcmQueueService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onTopic(SendMessageByFcmTopicRequest sendMessageByFcmTopicRequest) {
        fcmQueueService.enqueueTopic(sendMessageByFcmTopicRequest);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onToken(SendMessageByFcmTokenRequest sendMessageByFcmTokenRequest) {
        fcmQueueService.enqueueToken(sendMessageByFcmTokenRequest);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onTokens(SendMessageByFcmTokensRequest sendMessageByFcmTokensRequest) {
        fcmQueueService.enqueueTokens(sendMessageByFcmTokensRequest);
    }
}
