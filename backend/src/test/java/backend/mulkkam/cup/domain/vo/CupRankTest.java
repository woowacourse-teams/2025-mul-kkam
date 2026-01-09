package backend.mulkkam.cup.domain.vo;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_CUP_RANK_VALUE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertThrows;

import backend.mulkkam.common.exception.CommonException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("CupRank 도메인")
class CupRankTest {

    @DisplayName("우선순위 생성")
    @Nested
    class Creation {

        @DisplayName("우선순위가 1~3 사이이면 생성에 성공한다")
        @ParameterizedTest(name = "우선순위 = {0}")
        @ValueSource(ints = {1, 2, 3})
        void rank_within_valid_range_is_created(int value) {
            // when & then
            assertThatCode(() -> new CupRank(value))
                    .doesNotThrowAnyException();
        }

        @DisplayName("우선순위가 범위를 벗어나면 생성에 실패한다")
        @ParameterizedTest(name = "우선순위 = {0}")
        @ValueSource(ints = {0, -1, 4, 5})
        void rank_outside_valid_range_is_invalid(int value) {
            // when
            CommonException ex = assertThrows(CommonException.class,
                    () -> new CupRank(value));

            // then
            assertThat(ex.getErrorCode()).isEqualTo(INVALID_CUP_RANK_VALUE);
        }
    }

    @DisplayName("우선순위 승격")
    @Nested
    class Promote {

        @DisplayName("2위 또는 3위 컵은 한 단계 승격할 수 있다")
        @ParameterizedTest(name = "{0}위 → {0}-1위")
        @ValueSource(ints = {2, 3})
        void cup_can_be_promoted_to_higher_priority(int from) {
            // given
            CupRank sut = new CupRank(from);
            CupRank expected = new CupRank(from - 1);

            // when
            CupRank result = sut.promote();

            // then
            assertThat(result).isEqualTo(expected);
        }

        @DisplayName("1위 컵은 더 이상 승격할 수 없다")
        @Test
        void highest_priority_cup_cannot_be_promoted() {
            // given
            CupRank sut = new CupRank(1);

            // when
            CommonException ex = assertThrows(CommonException.class,
                    sut::promote);

            // then
            assertThat(ex.getErrorCode()).isEqualTo(INVALID_CUP_RANK_VALUE);
        }
    }

    @DisplayName("우선순위 강등")
    @Nested
    class Demote {

        @DisplayName("1위 또는 2위 컵은 한 단계 강등할 수 있다")
        @ParameterizedTest(name = "{0}위 → {0}+1위")
        @ValueSource(ints = {1, 2})
        void cup_can_be_demoted_to_lower_priority(int from) {
            // given
            CupRank sut = new CupRank(from);
            CupRank expected = new CupRank(from + 1);

            // when
            CupRank result = sut.demote();

            // then
            assertThat(result).isEqualTo(expected);
        }

        @DisplayName("3위 컵은 더 이상 강등할 수 없다")
        @Test
        void lowest_priority_cup_cannot_be_demoted() {
            // given
            CupRank sut = new CupRank(3);

            // when
            CommonException ex = assertThrows(CommonException.class,
                    sut::demote);

            // then
            assertThat(ex.getErrorCode()).isEqualTo(INVALID_CUP_RANK_VALUE);
        }
    }
}
