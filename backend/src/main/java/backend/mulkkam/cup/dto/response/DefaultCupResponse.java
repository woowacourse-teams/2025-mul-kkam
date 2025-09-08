package backend.mulkkam.cup.dto.response;

import backend.mulkkam.cup.domain.DefaultCup;
import backend.mulkkam.cup.domain.IntakeType;
import backend.mulkkam.cup.domain.vo.CupEmojiUrl;
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
        @Schema(description = "이모지 url", example = "https:///example/com️")
        String emojiUrl
) {

    public DefaultCupResponse(DefaultCup defaultCup, CupEmojiUrl cupEmoji) {
            this(
                    defaultCup.getNickname().value(),
                    defaultCup.getAmount().value(),
                    defaultCup.getRank().value(),
                    defaultCup.getIntakeType(),
                    cupEmoji.value()
            );
    }
}
