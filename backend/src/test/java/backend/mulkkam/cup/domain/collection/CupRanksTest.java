package backend.mulkkam.cup.domain.collection;

import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.cup.domain.vo.CupRank;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static backend.mulkkam.common.exception.errorCode.ConflictErrorCode.DUPLICATED_CUP_RANKS;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CupRanksTest {

    @DisplayName("인스턴스 생성 시에")
    @Nested
    class NewCupRanks {

        @DisplayName("중복되는 우선순위가 있으면 생성이 불가능하다.")
        @Test
        void error_existsDuplicatedRanks() {
            // given
            Map<Long, CupRank> cupRank = Map.of(
                    1L, new CupRank(1),
                    2L, new CupRank(2),
                    3L, new CupRank(2)
            );

            // when & then
            assertThatThrownBy(() -> new CupRanks(cupRank))
                    .isInstanceOf(CommonException.class)
                    .hasMessage(DUPLICATED_CUP_RANKS.name());
        }
    }
}
