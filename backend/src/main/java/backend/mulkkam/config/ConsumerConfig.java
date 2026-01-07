package backend.mulkkam.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.web.client.RestTemplate;

/**
 * Consumer Worker 전용 설정
 */
@Configuration
@EnableRetry
@Profile("consumer")
public class ConsumerConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
