package backend.mulkkam.cup.dto.response;

import backend.mulkkam.cup.domain.CupEmoji;
import backend.mulkkam.cup.domain.DefaultCup;
import backend.mulkkam.cup.domain.IntakeType;
import backend.mulkkam.cup.dto.CupEmojiResponse;
import io.swagger.v3.oas.annotations.media.Schema;

public record DefaultCupResponse(
        @Schema(description = "컵 이름", example = "스타벅스 텀블러")
        String cupNickname,
        @Schema(description = "컵 용량(ml)", example = "500")
        Integer cupAmount,
        @Schema(description = "컵 우선순위", example = "3")
        Integer cupRank,
        @Schema(description = "음료 종류", implementation = IntakeType.class)
        IntakeType intakeType,
        CupEmojiResponse emoji
) {

    public DefaultCupResponse(DefaultCup defaultCup, CupEmoji cupEmoji) {
            this(
                    defaultCup.getNickname().value(),
                    defaultCup.getAmount().value(),
                    defaultCup.getRank().value(),
                    defaultCup.getIntakeType(),
                    new CupEmojiResponse(cupEmoji)
            );
    }
}
