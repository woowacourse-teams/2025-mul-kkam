package backend.mulkkam.intake.domain.vo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ExtraIntakeTargetAmountTest {

    @DisplayName("평균 기온을 통해 추천 추가 음용량을 계산할 때")
    @Nested
    class CalculateWithAverageTemperature {

        @DisplayName("유효한 값들을 통해 정상적으로 계산된다")
        @Test
        void success_withValidValues() {
            // given
            double weight = 70.0;
            double averageTemperature = 27.0;

            // when
            ExtraIntakeAmount actual = ExtraIntakeAmount.calculateWithAverageTemperature(averageTemperature,
                    weight);

            // then
            assertThat(actual.value()).isEqualTo(350);
        }
    }
}
