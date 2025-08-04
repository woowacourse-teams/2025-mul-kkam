package backend.mulkkam.cup.domain.collection;

import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.cup.domain.vo.CupRank;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static backend.mulkkam.common.exception.errorCode.ErrorCode.DUPLICATED_CUP_RANKS;

public class CupRanks {

    private final Map<Long, CupRank> ranks;

    public CupRanks(Map<Long, CupRank> ranks) {
        validateRanksDuplicated(ranks);
        this.ranks = ranks;
    }

    private void validateRanksDuplicated(Map<Long, CupRank> ranks) {
        Set<CupRank> distinctRanks = new HashSet<>(ranks.values());
        if (distinctRanks.size() != ranks.size()) {
            throw new CommonException(DUPLICATED_CUP_RANKS);
        }
    }

    public Set<Long> getCupIds() {
        return Collections.unmodifiableSet(ranks.keySet());
    }

    public CupRank getCupRank(Long cupId) {
        return ranks.get(cupId);
    }
}
