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

            // 1) Heap 사용량(Used/Max) - area=heap만 허용
            if (name.equals("jvm.memory.used") || name.equals("jvm.memory.max")) {
                return "heap".equals(id.getTag("area"));
            }
            // 2) GC Pause
            if (name.equals("jvm.gc.pause")) {
                return true;
            }
            // 3) 스레드 개수
            if (name.equals("jvm.threads.live")) {
                return true;
            }

            return false;
        });
    }
}
