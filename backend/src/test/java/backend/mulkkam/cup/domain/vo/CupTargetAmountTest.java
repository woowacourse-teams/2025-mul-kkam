package backend.mulkkam.cup.domain.vo;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_CUP_AMOUNT;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import backend.mulkkam.common.exception.CommonException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class CupTargetAmountTest {

    @DisplayName("생성자 검증 시에")
    @Nested
    class NewCupTargetAmount {

        @DisplayName("1부터 20_000까지 설정할 수 있다")
        @ParameterizedTest
        @ValueSource(ints = {1, 500, 1_000, 1_500, 2_000})
        void success_amountBetween1And10000(Integer input) {
            // when & then
            assertThatCode(() -> {
                new CupAmount(input);
            }).doesNotThrowAnyException();
        }

        @DisplayName("범위를 벗어난 음용량을 설정할 수 없다")
        @ParameterizedTest
        @ValueSource(ints = {-1, 0, 2_001, 2_500})
        void error_nameLengthOutOfRange(Integer input) {
            // when & then
            assertThatThrownBy(() -> new CupAmount(input))
                    .isInstanceOf(CommonException.class)
                    .hasMessage(INVALID_CUP_AMOUNT.name());
        }
    }
}
