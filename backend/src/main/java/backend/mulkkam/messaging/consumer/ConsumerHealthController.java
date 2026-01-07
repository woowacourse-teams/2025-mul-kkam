package backend.mulkkam.messaging.consumer;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/health")
@Profile("consumer")
public class ConsumerHealthController {

    private final RabbitTemplate rabbitTemplate;

    public ConsumerHealthController(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @GetMapping
    public ResponseEntity<Map<String, String>> health() {
        boolean rabbitConnected = checkRabbitConnection();

        if (rabbitConnected) {
            return ResponseEntity.ok(Map.of(
                    "status", "UP",
                    "rabbitmq", "connected"
            ));
        } else {
            return ResponseEntity.status(503).body(Map.of(
                    "status", "DOWN",
                    "rabbitmq", "disconnected"
            ));
        }
    }

    private boolean checkRabbitConnection() {
        try {
            rabbitTemplate.execute(channel -> {
                channel.basicQos(1);
                return null;
            });
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
