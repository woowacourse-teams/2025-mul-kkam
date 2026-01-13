package backend.mulkkam.intake.domain.vo;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_TARGET_AMOUNT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertThrows;

import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.member.domain.vo.TargetAmount;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class TargetAmountTest {

    @DisplayName("목표량이 200~5000ml 사이이면 생성에 성공한다")
    @ParameterizedTest(name = "목표량 = {0}ml")
    @ValueSource(ints = {200, 1_000, 2_000, 5_000})
    void target_amount_within_valid_range_is_created(int value) {
        // when & then
        assertThatCode(() -> new TargetAmount(value))
                .doesNotThrowAnyException();
    }

    @DisplayName("목표량이 200ml 미만이면 생성에 실패한다")
    @ParameterizedTest(name = "목표량 = {0}ml")
    @ValueSource(ints = {-1, 0, 199})
    void target_amount_less_than_min_is_invalid(int value) {
        // when
        CommonException ex = assertThrows(CommonException.class,
                () -> new TargetAmount(value));

        // then
        assertThat(ex.getErrorCode()).isEqualTo(INVALID_TARGET_AMOUNT);
    }

    @DisplayName("목표량이 5000ml 초과이면 생성에 실패한다")
    @ParameterizedTest(name = "목표량 = {0}ml")
    @ValueSource(ints = {5_001, 10_000})
    void target_amount_greater_than_max_is_invalid(int value) {
        // when
        CommonException ex = assertThrows(CommonException.class,
                () -> new TargetAmount(value));

        // then
        assertThat(ex.getErrorCode()).isEqualTo(INVALID_TARGET_AMOUNT);
    }
}
