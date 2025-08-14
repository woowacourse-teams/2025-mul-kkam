package backend.mulkkam.intake.domain.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import backend.mulkkam.member.domain.vo.TargetAmount;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class AchievementRateTest {

    @DisplayName("AchievementRate 를 생성할 때 ")
    @Nested
    class NewAchievementRate {

        @DisplayName("정상적으로 결과를 반환한다")
        @Test
        void success_withValidAmount() {
            // given
            TargetAmount targetIntakeAmount = new TargetAmount(1_000);
            TargetAmount totalIntakeAmount = new TargetAmount(200);

            // when
            AchievementRate achievementRate = new AchievementRate(
                    totalIntakeAmount,
                    targetIntakeAmount
            );

            // then
            assertThat(achievementRate.value())
                    .isCloseTo(10.0, within(0.1));
        }

        @DisplayName("100이 넘는 경우 100을 반환한다")
        @Test
        void success_withValueOver100() {
            // given
            TargetAmount targetIntakeAmount = new TargetAmount(1_000);
            TargetAmount totalIntakeAmount = new TargetAmount(5_000);

            // when
            AchievementRate achievementRate = new AchievementRate(
                    totalIntakeAmount,
                    targetIntakeAmount
            );

            // then
            assertThat(achievementRate.value())
                    .isEqualTo(100);
        }
    }
}
