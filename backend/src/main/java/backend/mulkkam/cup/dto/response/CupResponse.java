package backend.mulkkam.cup.dto.response;

import backend.mulkkam.cup.domain.Cup;
import backend.mulkkam.cup.domain.IntakeType;
import io.swagger.v3.oas.annotations.media.Schema;

public record CupResponse(
        @Schema(description = "컵 식별자", example = "1")
        Long id,
        @Schema(description = "컵 이름", example = "스타벅스 텀블러")
        String cupNickname,
        @Schema(description = "컵 용량(ml)", example = "500")
        Integer cupAmount,
        @Schema(description = "컵 우선순위", example = "3")
        Integer cupRank,
        @Schema(description = "음료 종류", implementation = IntakeType.class)
        IntakeType intakeType,
        @Schema(description = "이모지", example = "☕️")
        String emoji
) {

    public CupResponse(Cup cup) {
        this(
                cup.getId(),
                cup.getNickname().value(),
                cup.getCupAmount().value(),
                cup.getCupRank().value(),
                cup.getIntakeType(),
                cup.getEmoji()
        );
    }
}
