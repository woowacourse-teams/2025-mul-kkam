package backend.mulkkam.intake.domain.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class RecommendAmountTest {

    @DisplayName("생성자 검증 시에")
    @Nested
    class NewRecommendAmount {

        @DisplayName("올바른 추천 음용량을 제공한다.")
        @ParameterizedTest
        @ValueSource(ints = {200, 1_000, 2_000, 5_000})
        void success_validValue(int input) {
            // given

            // when & then
            assertThatCode(() -> {
                new RecommendAmount(input);
            }).doesNotThrowAnyException();
        }

        @DisplayName("5000ml를 넘어가는 음용량은 5000ml로 추천한다.")
        @ParameterizedTest
        @ValueSource(ints = {5_001, 6_000, 10_000})
        void success_greaterThanMaxValue(int input) {
            // given

            // when
            RecommendAmount recommendAmount = new RecommendAmount(input);

            // then
            assertThat(recommendAmount.value()).isEqualTo(5000);
        }

        @DisplayName("200ml 보다 낮다면, 음용량은 200ml로 추천한다.")
        @ParameterizedTest
        @ValueSource(ints = {199, 100, 50, 1, -40})
        void success_lessThanMinValue(int input) {
            // given

            // when
            RecommendAmount recommendAmount = new RecommendAmount(input);

            // then
            assertThat(recommendAmount.value()).isEqualTo(200);
        }
    }
}
