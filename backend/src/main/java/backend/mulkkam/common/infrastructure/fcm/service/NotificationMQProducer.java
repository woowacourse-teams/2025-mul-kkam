package backend.mulkkam.common.infrastructure.fcm.service;

import backend.mulkkam.common.infrastructure.fcm.dto.request.SendMessageByFcmTokenRequest;
import backend.mulkkam.common.infrastructure.fcm.dto.request.SendMessageByFcmTokensRequest;
import backend.mulkkam.common.infrastructure.fcm.dto.request.SendMessageByFcmTopicRequest;
import com.google.common.collect.Lists;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class NotificationMQProducer {

    private final RabbitTemplate rabbitTemplate;

    @Value("${mulkkam.mq.exchange}")
    private String exchange;

    private static final int FCM_BATCH_SIZE = 500;

    public void publishToken(SendMessageByFcmTokenRequest req) {
        log.info("Publishing token to fcm queue");
        rabbitTemplate.convertAndSend(exchange, "notify.token", req);
    }

    public void publishTokens(SendMessageByFcmTokensRequest req) {
        log.info("Publishing token to fcm queue");
        for (List<String> part : Lists.partition(req.allTokens(), FCM_BATCH_SIZE)) {
            rabbitTemplate.convertAndSend(exchange, "notify.tokens", req.withTokens(part));
        }
    }

    public void publishTopic(SendMessageByFcmTopicRequest req) {
        log.info("Publishing token to fcm queue");
        rabbitTemplate.convertAndSend(exchange, "notify.topic", req);
    }
}