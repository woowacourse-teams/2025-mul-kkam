package backend.mulkkam.common.config;

import backend.mulkkam.intake.service.IntakeAmountCalculator;
import backend.mulkkam.intake.service.SimpleWeightBasedCalculator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IntakeAmountPolicyConfig {

    @Bean
    public IntakeAmountCalculator intakeAmountCalculator() {
        return new SimpleWeightBasedCalculator();
    }
}
