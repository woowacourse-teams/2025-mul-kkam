package backend.mulkkam.cup.domain.collection;

import static backend.mulkkam.common.exception.errorCode.ConflictErrorCode.DUPLICATED_CUP_RANKS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertThrows;

import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.cup.domain.vo.CupRank;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CupRanksTest {

    @DisplayName("모든 컵의 우선순위가 고유하면 생성에 성공한다")
    @Test
    void cup_ranks_with_unique_priorities_is_created() {
        // given
        Map<Long, CupRank> ranks = Map.of(
                1L, new CupRank(1),
                2L, new CupRank(2),
                3L, new CupRank(3)
        );

        // when & then
        assertThatCode(() -> new CupRanks(ranks))
                .doesNotThrowAnyException();
    }

    @DisplayName("중복된 우선순위가 있으면 생성에 실패한다")
    @Test
    void cup_ranks_with_duplicate_priorities_is_invalid() {
        // given
        Map<Long, CupRank> ranks = Map.of(
                1L, new CupRank(1),
                2L, new CupRank(2),
                3L, new CupRank(2)
        );

        // when
        CommonException ex = assertThrows(CommonException.class,
                () -> new CupRanks(ranks));

        // then
        assertThat(ex.getErrorCode()).isEqualTo(DUPLICATED_CUP_RANKS);
    }
}
