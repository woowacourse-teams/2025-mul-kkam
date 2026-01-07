package backend.mulkkam.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Consumer Worker에서만 스케줄링 활성화
 * Inbox Cleanup 스케줄러 등 Consumer 전용 스케줄러를 위함
 */
@Configuration
@EnableScheduling
@Profile("consumer")
public class ConsumerSchedulingConfig {
}
