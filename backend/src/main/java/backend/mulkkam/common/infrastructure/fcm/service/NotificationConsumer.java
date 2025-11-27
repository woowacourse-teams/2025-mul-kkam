package backend.mulkkam.common.infrastructure.fcm.service;

import backend.mulkkam.common.infrastructure.fcm.dto.request.SendMessageByFcmTokenRequest;
import backend.mulkkam.common.infrastructure.fcm.dto.request.SendMessageByFcmTokensRequest;
import backend.mulkkam.common.infrastructure.fcm.dto.request.SendMessageByFcmTopicRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("worker")
@RequiredArgsConstructor
@Component
public class NotificationConsumer {

    private final FcmClient fcmClient;

    // 단일 토큰 기반 발송
    @RabbitListener(
            queues = "${mulkkam.mq.queue.token}",
            containerFactory = "rabbitListenerContainerFactory"
    )
    public void onToken(SendMessageByFcmTokenRequest req) {
        fcmClient.sendMessageByToken(req);
    }

    // 멀티 캐스트 기반 발송
    @RabbitListener(
            queues = "${mulkkam.mq.queue.tokens}",
            containerFactory = "rabbitListenerContainerFactory"
    )
    public void onTokens(SendMessageByFcmTokensRequest req) {
        fcmClient.sendMulticast(req);
    }

    // 토픽 발송
    @RabbitListener(
            queues = "${mulkkam.mq.queue.topic}",
            containerFactory = "rabbitListenerContainerFactory"
    )
    public void onTopic(SendMessageByFcmTopicRequest req) {
        fcmClient.sendMessageByTopic(req);
    }
}