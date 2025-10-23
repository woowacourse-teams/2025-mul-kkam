package backend.mulkkam.common.config;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@EnableRabbit
@Configuration
@Profile("worker")
public class RabbitWorkerConfig {

    @Bean("fcmWorkerExecutor")
    public ThreadPoolTaskExecutor fcmWorkerExecutor() {
        var ex = new ThreadPoolTaskExecutor();
        ex.setCorePoolSize(4);
        ex.setMaxPoolSize(8);
        ex.setQueueCapacity(200);
        ex.setThreadNamePrefix("fcm-worker-");
        ex.initialize();
        return ex;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory cf,
            @Qualifier("fcmWorkerExecutor") TaskExecutor exec,
            Jackson2JsonMessageConverter converter
    ) {
        var f = new SimpleRabbitListenerContainerFactory();
        f.setConnectionFactory(cf);
        f.setMessageConverter(converter);
        f.setConcurrentConsumers(4);
        f.setMaxConcurrentConsumers(8);
        f.setPrefetchCount(100);
        f.setTaskExecutor(exec);
        f.setDefaultRequeueRejected(false);
        return f;
    }
}