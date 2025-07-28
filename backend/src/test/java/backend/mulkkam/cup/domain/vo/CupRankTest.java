package backend.mulkkam.cup.domain.vo;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_CUP_SIZE;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import backend.mulkkam.common.exception.CommonException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class CupRankTest {

    @DisplayName("생성자 검증 시에")
    @Nested
    class NewCupNickname {

        @DisplayName("컵의 갯수는 3개 이하면 가능하다")
        @ParameterizedTest
        @ValueSource(ints = {0, 1, 2, 3})
        void success_nameLengthBetween2And10(Integer input) {
            // when & then
            assertThatCode(() -> {
                new CupRank(input);
            }).doesNotThrowAnyException();
        }

        @DisplayName("범위를 벗어난 갯수는 설정할 수 없다")
        @ParameterizedTest
        @ValueSource(ints = {4, -1, 5})
        void error_nameLengthOutOfRange(Integer input) {
            // when & then
            CommonException ex = assertThrows(CommonException.class,
                    () -> new CupRank(input));
            assertThat(ex.getErrorCode()).isEqualTo(INVALID_CUP_SIZE);
        }
    }
}
