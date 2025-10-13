package backend.mulkkam.common.infrastructure.fcm.service;

import backend.mulkkam.common.infrastructure.fcm.dto.request.SendMessageByFcmTokenRequest;
import backend.mulkkam.common.infrastructure.fcm.dto.request.SendMessageByFcmTokensRequest;
import backend.mulkkam.common.infrastructure.fcm.dto.request.SendMessageByFcmTopicRequest;
import com.google.common.collect.Lists;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Async
@RequiredArgsConstructor
@Component
public class FcmEventListener {

    private final FcmClient fcmClient;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @EventListener
    public void onTopic(SendMessageByFcmTopicRequest sendMessageByFcmTopicRequest) {
        fcmClient.sendMessageByTopic(sendMessageByFcmTopicRequest);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @EventListener
    public void onToken(SendMessageByFcmTokenRequest sendMessageByFcmTokenRequest) {
        fcmClient.sendMessageByToken(sendMessageByFcmTokenRequest);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @EventListener
    public void onTokens(SendMessageByFcmTokensRequest sendMessageByFcmTokensRequest) {
        List<String> allTokens = sendMessageByFcmTokensRequest.tokens();
        Lists.partition(allTokens, 500).forEach(tokens -> {
            SendMessageByFcmTokensRequest request =
                    sendMessageByFcmTokensRequest.withTokens(tokens);
            fcmClient.sendMulticast(request);
        });
    }
}
