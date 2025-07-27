package backend.mulkkam.cup.domain.vo;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_CUP_AMOUNT;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import backend.mulkkam.common.exception.CommonException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class CupAmountTest {

    @DisplayName("생성자 검증 시에")
    @Nested
    class NewCupAmount {

        @DisplayName("1부터 10000까지 설정할 수 있다")
        @ParameterizedTest
        @ValueSource(ints = {1, 500, 1_000, 5_000, 10_000})
        void success_amountBetween1And10000(Integer input) {
            // when & then
            assertThatCode(() -> {
                new CupAmount(input);
            }).doesNotThrowAnyException();
        }

        @DisplayName("범위를 벗어난 음용량을 설정할 수 없다")
        @ParameterizedTest
        @ValueSource(ints = {-1, 0, 10_001, 150_000})
        void error_nameLengthOutOfRange(Integer input) {
            // when & then
            CommonException ex = assertThrows(CommonException.class,
                    () -> new CupAmount(input));
            assertThat(ex.getErrorCode()).isEqualTo(INVALID_CUP_AMOUNT);
        }
    }
}
