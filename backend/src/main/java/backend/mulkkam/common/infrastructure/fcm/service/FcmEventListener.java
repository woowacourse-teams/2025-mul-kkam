package backend.mulkkam.common.infrastructure.fcm.service;

import backend.mulkkam.common.infrastructure.fcm.dto.SendTokenEvent;
import backend.mulkkam.common.infrastructure.fcm.dto.SendTopicEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class FcmEventListener {

    private final FcmClient fcmClient;

    @EventListener
    public void onTopic(SendTopicEvent sendTopicEvent) {
        fcmClient.sendMessageByTopic(sendTopicEvent.sendMessageByFcmTopicRequest());
    }

    @EventListener
    public void onToken(SendTokenEvent sendTokenEvent) {
        fcmClient.sendMessageByToken(sendTokenEvent.sendMessageByFcmTokenRequest());
    }
}
