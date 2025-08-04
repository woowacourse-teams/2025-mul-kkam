package backend.mulkkam.cup.dto;

import backend.mulkkam.cup.domain.Cup;
import backend.mulkkam.cup.domain.dto.CupRankDto;

public record UpdateCupRankDto(
        Long id,
        Integer rank
) {
    public CupRankDto toCupRankDto() {
        return new CupRankDto(id, rank);
    }

    public UpdateCupRankDto(Cup cup) {
        this(cup.getId(), cup.getCupRank().value());
    }
}
