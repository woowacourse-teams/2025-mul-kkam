package backend.mulkkam.cup.domain.vo;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class CupAmountTest {

    @Nested
    @DisplayName("생성자 검증 시에")
    class NewCupAmount {

        @DisplayName("0부터 10000까지 설정할 수 있다")
        @ParameterizedTest
        @ValueSource(strings = {"0", "500", "1000", "5000", "10000"})
        void success_amountBetween0And10000(Integer input) {
            // when & then
            assertThatCode(() -> {
                new CupAmount(input);
            }).doesNotThrowAnyException();
        }

        @DisplayName("범위를 벗어난 음용량을 설정할 수 없다")
        @ParameterizedTest
        @ValueSource(strings = {"-1", "10001", "150000"})
        void error_nameLengthOutOfRange(Integer input) {
            // when & then
            assertThatThrownBy(() -> {
                new CupAmount(input);
            }).isInstanceOf(IllegalArgumentException.class);
        }
    }
}
