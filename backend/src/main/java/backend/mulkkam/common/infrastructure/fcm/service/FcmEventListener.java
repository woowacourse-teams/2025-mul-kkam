package backend.mulkkam.common.infrastructure.fcm.service;

import backend.mulkkam.common.infrastructure.fcm.dto.request.SendMessageByFcmTokenRequest;
import backend.mulkkam.common.infrastructure.fcm.dto.request.SendMessageByFcmTokensRequest;
import backend.mulkkam.common.infrastructure.fcm.dto.request.SendMessageByFcmTopicRequest;
import com.google.common.collect.Lists;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class FcmEventListener { // TODO: Async 처리

    private final FcmClient fcmClient;

    @EventListener
    public void onTopic(SendMessageByFcmTopicRequest sendMessageByFcmTopicRequest) {
        fcmClient.sendMessageByTopic(sendMessageByFcmTopicRequest);
    }

    @EventListener
    public void onToken(SendMessageByFcmTokenRequest sendMessageByFcmTokenRequest) {
        fcmClient.sendMessageByToken(sendMessageByFcmTokenRequest);
    }

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
