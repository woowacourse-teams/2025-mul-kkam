package backend.mulkkam.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@Profile({"prod", "dev"})
@EnableAsync
public class WebAsyncConfig {

    @Bean("producerExecutor")
    public ThreadPoolTaskExecutor producerExecutor() {
        var ex = new ThreadPoolTaskExecutor();
        ex.setCorePoolSize(2);
        ex.setMaxPoolSize(4);
        ex.setQueueCapacity(50);
        ex.setThreadNamePrefix("producer-async-");
        ex.initialize();
        return ex;
    }

}
