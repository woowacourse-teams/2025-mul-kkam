package backend.mulkkam.intake.domain.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class AchievementRateTest {

    @DisplayName("AchievementRate 를 생성할 때 ")
    @Nested
    class NewAchievementRate {

        @DisplayName("정상적으로 결과를 반환한다")
        @Test
        void success_withValidAmount() {
            // given
            Amount targetIntakeAmount = new Amount(1_000);
            Amount totalIntakeAmount = new Amount(100);

            // when
            AchievementRate achievementRate = new AchievementRate(
                    totalIntakeAmount,
                    targetIntakeAmount
            );

            // then
            assertThat(achievementRate.value())
                    .isCloseTo(10.0, within(0.1));
        }
    }
}
