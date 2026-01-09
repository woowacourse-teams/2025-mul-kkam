package backend.mulkkam.cup.domain.vo;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_CUP_AMOUNT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertThrows;

import backend.mulkkam.common.exception.CommonException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("CupAmount 도메인")
class CupTargetAmountTest {

    @DisplayName("용량이 1~2000ml 사이이면 생성에 성공한다")
    @ParameterizedTest(name = "용량 = {0}ml")
    @ValueSource(ints = {1, 500, 1_000, 1_500, 2_000})
    void amount_within_valid_range_is_created(int value) {
        // when & then
        assertThatCode(() -> new CupAmount(value))
                .doesNotThrowAnyException();
    }

    @DisplayName("용량이 0 이하이면 생성에 실패한다")
    @ParameterizedTest(name = "용량 = {0}ml")
    @ValueSource(ints = {-1, 0})
    void amount_less_than_min_is_invalid(int value) {
        // when
        CommonException ex = assertThrows(CommonException.class,
                () -> new CupAmount(value));

        // then
        assertThat(ex.getErrorCode()).isEqualTo(INVALID_CUP_AMOUNT);
    }

    @DisplayName("용량이 2000ml 초과이면 생성에 실패한다")
    @ParameterizedTest(name = "용량 = {0}ml")
    @ValueSource(ints = {2_001, 2_500, 3_000})
    void amount_greater_than_max_is_invalid(int value) {
        // when
        CommonException ex = assertThrows(CommonException.class,
                () -> new CupAmount(value));

        // then
        assertThat(ex.getErrorCode()).isEqualTo(INVALID_CUP_AMOUNT);
    }
}
