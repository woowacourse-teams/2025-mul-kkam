package backend.mulkkam.cup.domain.vo;

import backend.mulkkam.common.exception.CommonException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_CUP_COUNT;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CupRankTest {

    @DisplayName("생성자 검증 시에")
    @Nested
    class NewCupNickname {

        @DisplayName("컵의 우선순위는 1 이상 3 이하만 가능하다")
        @ParameterizedTest
        @ValueSource(ints = {1, 2, 3})
        void success_nameLengthBetween2And10(Integer input) {
            // when & then
            assertThatCode(() -> {
                new CupRank(input);
            }).doesNotThrowAnyException();
        }

        @DisplayName("범위를 벗어난 갯수는 설정할 수 없다")
        @ParameterizedTest
        @ValueSource(ints = {0, 4, -1, 5})
        void error_nameLengthOutOfRange(Integer input) {
            // when & then
            CommonException ex = assertThrows(CommonException.class,
                    () -> new CupRank(input));
            assertThat(ex.getErrorCode()).isEqualTo(INVALID_CUP_COUNT);
        }
    }

    @DisplayName("우선순위를 높일 시에")
    @Nested
    class Promote {

        @DisplayName("한 단계 높은 우선순위의 랭크를 반환한다.")
        @ParameterizedTest
        @ValueSource(ints = {2, 3})
        void success_valueBetween2And3(int from) {
            // given
            CupRank origin = new CupRank(from);
            CupRank expected = new CupRank(from - 1);

            // when
            CupRank resultRank = origin.promote();

            // then
            assertThat(resultRank).isEqualTo(expected);
        }

        @DisplayName("이미 가장 높은 우선순위일 경우 예외가 발생한다.")
        @Test
        void error_valueIsHighestPriority() {
            // given
            CupRank originRank = new CupRank(1);

            // when & then
            assertThatThrownBy(originRank::promote)
                    .isInstanceOf(CommonException.class)
                    .hasMessage(INVALID_CUP_COUNT.name());
        }
    }

    @DisplayName("우선순위를 낮출 시에")
    @Nested
    class Demote {

        @DisplayName("한 단계 낮은 우선순위의 랭크를 반환한다.")
        @ParameterizedTest
        @ValueSource(ints = {1, 2})
        void success_valueBetween2And3(int from) {
            // given
            CupRank origin = new CupRank(from);
            CupRank expected = new CupRank(from + 1);

            // when
            CupRank resultRank = origin.demote();

            // then
            assertThat(resultRank).isEqualTo(expected);
        }

        @DisplayName("이미 가장 낮은 우선순위일 경우 예외가 발생한다.")
        @Test
        void error_valueIsHighestPriority() {
            // given
            CupRank originRank = new CupRank(3);

            // when & then
            assertThatThrownBy(originRank::demote)
                    .isInstanceOf(CommonException.class)
                    .hasMessage(INVALID_CUP_COUNT.name());
        }
    }
}
