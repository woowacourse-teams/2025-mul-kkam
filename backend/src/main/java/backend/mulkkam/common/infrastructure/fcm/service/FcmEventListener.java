package backend.mulkkam.common.infrastructure.fcm.service;

import backend.mulkkam.common.infrastructure.fcm.dto.request.SendMessageByFcmTokenRequest;
import backend.mulkkam.common.infrastructure.fcm.dto.request.SendMessageByFcmTopicRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class FcmEventListener {

    private final FcmClient fcmClient;

    // TODO: 알림 성능 테스트 이후 삭제
    @Async
    @EventListener
    public void onTopic(SendMessageByFcmTopicRequest sendMessageByFcmTopicRequest) {
        fcmClient.sendMessageByTopic(sendMessageByFcmTopicRequest);
    }

    // TODO: 알림 성능 테스트 이후 삭제
    @Async
    @EventListener
    public void onToken(SendMessageByFcmTokenRequest sendMessageByFcmTokenRequest) {
        fcmClient.sendMessageByToken(sendMessageByFcmTokenRequest);
    }
}
