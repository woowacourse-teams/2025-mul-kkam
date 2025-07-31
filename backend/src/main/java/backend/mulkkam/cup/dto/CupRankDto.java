package backend.mulkkam.cup.dto;

import backend.mulkkam.cup.domain.Cup;

public record CupRankDto(
        Long id,
        Integer rank
) {
    public CupRankDto(Cup cup) {
        this(cup.getId(), cup.getCupRank().value());
    }
}
