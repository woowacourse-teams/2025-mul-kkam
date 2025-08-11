package backend.mulkkam.cup.dto;

import backend.mulkkam.cup.domain.Cup;
import jakarta.validation.constraints.NotNull;

public record CupRankDto(
        Long id,
        @NotNull Integer rank
) {

    public CupRankDto(Cup cup) {
        this(cup.getId(), cup.getCupRank().value());
    }
}
