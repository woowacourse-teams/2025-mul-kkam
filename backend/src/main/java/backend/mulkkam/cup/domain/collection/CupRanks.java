package backend.mulkkam.cup.domain.collection;

import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.cup.domain.dto.CupRankDto;
import backend.mulkkam.cup.domain.vo.CupRank;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static backend.mulkkam.common.exception.errorCode.ConflictErrorCode.DUPLICATED_CUP;
import static backend.mulkkam.common.exception.errorCode.ConflictErrorCode.DUPLICATED_CUP_RANKS;

public class CupRanks {

    private final Map<Long, CupRank> ranks;

    public CupRanks(Map<Long, CupRank> ranks) {
        validateRanksDuplicated(ranks);
        this.ranks = ranks;
    }

    public CupRanks(List<CupRankDto> cupRanks) {
        this(convert(cupRanks));
    }

    private void validateRanksDuplicated(Map<Long, CupRank> ranks) {
        Set<CupRank> distinctRanks = new HashSet<>(ranks.values());
        if (distinctRanks.size() != ranks.size()) {
            throw new CommonException(DUPLICATED_CUP_RANKS);
        }
    }

    private static Map<Long, CupRank> convert(List<CupRankDto> cupRanks) {
        Map<Long, CupRank> ranks = new HashMap<>();
        for (CupRankDto cup : cupRanks) {
            if (ranks.containsKey(cup.id())) {
                throw new CommonException(DUPLICATED_CUP);
            }
            ranks.put(cup.id(), new CupRank(cup.rank()));
        }
        return ranks;
    }

    public Set<Long> getCupIds() {
        return Collections.unmodifiableSet(ranks.keySet());
    }

    public CupRank getCupRank(Long cupId) {
        return ranks.get(cupId);
    }
}
