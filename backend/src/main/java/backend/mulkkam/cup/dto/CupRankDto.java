package backend.mulkkam.cup.dto;

import backend.mulkkam.cup.domain.Cup;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;

public record CupRankDto(
        @Schema(description = "컵 id", example = "1")
        Long id,
        @Schema(description = "컵 순위", example = "2")
        @NotNull Integer rank
) {

    public CupRankDto(Cup cup) {
        this(cup.getId(), cup.getCupRank().value());
    }
}
