package backend.mulkkam.common.config;


import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("worker")
@Configuration
public class RabbitMQConfig {

    @Value("${mulkkam.mq.exchange}")
    private String ex;

    @Value("${mulkkam.mq.queue.token}")
    private String qToken;

    @Value("${mulkkam.mq.queue.tokens}")
    private String qTokens;

    @Value("${mulkkam.mq.queue.topic}")
    private String qTopic;

    @Bean
    public TopicExchange notificationsExchange() {
        return new TopicExchange(ex, true, false);
    }

    @Bean
    public Queue tokenQueue() {
        return QueueBuilder.durable(qToken).build();
    }

    @Bean
    public Queue tokensQueue() {
        return QueueBuilder.durable(qTokens).build();
    }

    @Bean
    public Queue topicQueue() {
        return QueueBuilder.durable(qTopic).build();
    }

    @Bean
    public Binding bindToken() {
        return BindingBuilder.bind(tokenQueue()).to(notificationsExchange()).with("notify.token");
    }

    @Bean
    public Binding bindTokens() {
        return BindingBuilder.bind(tokensQueue()).to(notificationsExchange()).with("notify.tokens");
    }

    @Bean
    public Binding bindTopic() {
        return BindingBuilder.bind(topicQueue()).to(notificationsExchange()).with("notify.topic");
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory cf,
                                         Jackson2JsonMessageConverter converter) {
        RabbitTemplate t = new RabbitTemplate(cf);
        t.setMessageConverter(converter);
        return t;
    }
}