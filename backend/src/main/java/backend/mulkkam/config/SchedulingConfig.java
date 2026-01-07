package backend.mulkkam.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * API Server에서만 스케줄링 활성화
 * Consumer 프로파일에서는 ReminderScheduleService 등의 스케줄러가 동작하지 않음
 */
@Configuration
@EnableScheduling
@Profile("!consumer")
public class SchedulingConfig {
}
