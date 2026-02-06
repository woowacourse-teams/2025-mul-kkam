package backend.mulkkam.intake.domain.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import backend.mulkkam.member.domain.vo.TargetAmount;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class AchievementRateTest {

    @DisplayName("달성률이 정확히 계산된다")
    @ParameterizedTest(name = "섭취 {0}ml / 목표 {1}ml = {2}%")
    @CsvSource({
            "0, 1000, 0.0",
            "200, 1000, 20.0",
            "500, 1000, 50.0",
            "1000, 1000, 100.0"
    })
    void achievement_rate_is_calculated_correctly(int intake, int target, double expectedRate) {
        // given
        TargetAmount targetAmount = new TargetAmount(target);

        // when
        AchievementRate result = new AchievementRate(intake, targetAmount);

        // then
        assertThat(result.value()).isCloseTo(expectedRate, within(0.1));
    }

    @DisplayName("섭취량이 목표량을 초과해도 달성률은 100%로 제한된다")
    @ParameterizedTest(name = "섭취 {0}ml / 목표 {1}ml = 100% (실제 {2}%)")
    @CsvSource({
            "1500, 1000, 150",
            "2000, 1000, 200",
            "5000, 1000, 500"
    })
    void achievement_rate_is_capped_at_100_percent(int intake, int target, int actualPercentage) {
        // given
        TargetAmount targetAmount = new TargetAmount(target);

        // when
        AchievementRate result = new AchievementRate(intake, targetAmount);

        // then
        assertThat(result.value()).isEqualTo(100.0);
    }
}
