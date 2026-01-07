package backend.mulkkam.messaging.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "rabbitmq.enabled", havingValue = "true", matchIfMissing = false)
public class RabbitMQConfig {

    @Value("${rabbitmq.exchange.notification:notification.exchange}")
    private String exchangeName;

    @Value("${rabbitmq.queue.notification.main:notification.queue}")
    private String mainQueueName;

    @Value("${rabbitmq.queue.notification.retry:notification.queue.retry}")
    private String retryQueueName;

    @Value("${rabbitmq.queue.notification.dlq:notification.queue.dlq}")
    private String dlqName;

    @Value("${rabbitmq.routing-key.notification:notification.routing-key}")
    private String routingKey;

    // ===== Exchange =====
    @Bean
    public DirectExchange notificationExchange() {
        return new DirectExchange(exchangeName, true, false);
    }

    // ===== Dead Letter Exchange =====
    @Bean
    public DirectExchange dlxExchange() {
        return new DirectExchange(exchangeName + ".dlx", true, false);
    }

    // ===== Main Queue (DLX 설정) =====
    @Bean
    public Queue notificationQueue() {
        return QueueBuilder.durable(mainQueueName)
                .withArgument("x-dead-letter-exchange", exchangeName + ".dlx")
                .withArgument("x-dead-letter-routing-key", routingKey + ".dlq")
                .build();
    }

    // ===== Retry Queue (TTL 후 Main Queue로 재전송) =====
    @Bean
    public Queue retryQueue() {
        return QueueBuilder.durable(retryQueueName)
                .withArgument("x-dead-letter-exchange", exchangeName)
                .withArgument("x-dead-letter-routing-key", routingKey)
                .withArgument("x-message-ttl", 5000) // 5초 후 재시도
                .build();
    }

    // ===== Dead Letter Queue =====
    @Bean
    public Queue dlq() {
        return QueueBuilder.durable(dlqName).build();
    }

    // ===== Bindings =====
    @Bean
    public Binding mainQueueBinding() {
        return BindingBuilder.bind(notificationQueue())
                .to(notificationExchange())
                .with(routingKey);
    }

    @Bean
    public Binding retryQueueBinding() {
        return BindingBuilder.bind(retryQueue())
                .to(dlxExchange())
                .with(routingKey + ".retry");
    }

    @Bean
    public Binding dlqBinding() {
        return BindingBuilder.bind(dlq())
                .to(dlxExchange())
                .with(routingKey + ".dlq");
    }

    // ===== Message Converter =====
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}
