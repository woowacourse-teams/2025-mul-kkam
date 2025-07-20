package backend.mulkkam.intake.domain.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AmountTest {
    
    @Nested
    @DisplayName("생성자 검증")
    class NewAmount {
        
        @DisplayName("올바른 음수량을 입력할 수 있다.")
        @ParameterizedTest
        @ValueSource(ints = {1, 10, 50, 100, 1000})
        void success_validateValue(int input) {
            // given

            // when & then
            assertThatCode(() -> {
                new Amount(input);
            }).doesNotThrowAnyException();
        }

        @DisplayName("범위를 벗어난 음수량은 입력할 수 없다.")
        @ParameterizedTest
        @ValueSource(ints = {-1})
        void error_invalidValue(int input) {
            // given

            // when & then
            assertThatThrownBy(() -> {
                new Amount(input);
            }).isInstanceOf(IllegalArgumentException.class);
        }
    }
}
