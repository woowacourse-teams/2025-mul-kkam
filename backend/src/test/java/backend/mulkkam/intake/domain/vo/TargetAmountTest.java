package backend.mulkkam.intake.domain.vo;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_TARGET_AMOUNT;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.member.domain.vo.TargetAmount;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class TargetAmountTest {


    @DisplayName("생성자 검증 시에")
    @Nested
    class NewTargetAmount {

        @DisplayName("올바른 음용량을 입력할 수 있다.")
        @ParameterizedTest
        @ValueSource(ints = {200, 1_000, 2_000, 5_000})
        void success_validValue(int input) {
            // given

            // when & then
            assertThatCode(() -> {
                new TargetAmount(input);
            }).doesNotThrowAnyException();
        }

        @DisplayName("범위를 벗어난 음용량은 입력할 수 없다.")
        @ParameterizedTest
        @ValueSource(ints = {-1, 0, 199, 5_001})
        void error_invalidValue(int input) {
            // given

            // when & then
            assertThatThrownBy(() -> new TargetAmount(input))
                    .isInstanceOf(CommonException.class)
                    .hasMessage(INVALID_TARGET_AMOUNT.name());
        }
    }
}
