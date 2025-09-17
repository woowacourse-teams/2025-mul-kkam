package backend.mulkkam.intake.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "권장 음용량 응답")
public record SuggestionIntakeAmountResponse(
        @Schema(description = "권장 음용량 (ml)", example = "2000", minimum = "1")
        int amount
) {
}
