package backend.mulkkam.messaging.producer;

import backend.mulkkam.messaging.dto.NotificationMessage;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
@Profile("!consumer") // API Server에서만 활성화
@ConditionalOnProperty(name = "rabbitmq.enabled", havingValue = "true", matchIfMissing = false)
public class NotificationMessageProducer {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.notification:notification.exchange}")
    private String exchangeName;

    @Value("${rabbitmq.routing-key.notification:notification.routing-key}")
    private String routingKey;

    public void publish(NotificationMessage message) {
        try {
            rabbitTemplate.convertAndSend(exchangeName, routingKey, message);
            log.info("[RABBITMQ PUBLISH] messageId={}, memberId={}, type={}",
                    message.messageId(), message.memberId(), message.type());
        } catch (Exception e) {
            log.error("[RABBITMQ PUBLISH FAILED] messageId={}, error={}",
                    message.messageId(), e.getMessage());
            // Producer에서는 재시도하지 않음 - 실패 시 로깅만
            throw e;
        }
    }

    public void publishBatch(List<NotificationMessage> messages) {
        messages.forEach(this::publish);
    }
}
