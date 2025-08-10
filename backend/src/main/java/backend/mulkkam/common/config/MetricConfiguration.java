package backend.mulkkam.common.config;

import io.micrometer.core.instrument.config.MeterFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricConfiguration {

    @Bean
    MeterFilter allowOnlyMetricsInUse() {
        return MeterFilter.denyUnless(id -> {
            String name = id.getName();

            // Heap (area=heap만)
            if (name.equals("jvm.memory.used") && "heap".equals(id.getTag("area"))) {
                return true;
            }
            if (name.equals("jvm.memory.max") && "heap".equals(id.getTag("area"))) {
                return true;
            }

            // GC Pause / Threads
            if (name.equals("jvm.gc.pause")) {
                return true;
            }
            if (name.equals("jvm.threads.live")) {
                return true;
            }

            // HikariCP 커넥션 풀 상태
            if (name.contains("hikaricp")) {
                return true; // 총계 시계열
            }
            return false;
        });
    }
}
