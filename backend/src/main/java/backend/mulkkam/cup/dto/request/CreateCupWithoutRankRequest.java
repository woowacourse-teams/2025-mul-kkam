package backend.mulkkam.cup.dto.request;

import backend.mulkkam.cup.domain.CupEmoji;
import backend.mulkkam.cup.domain.IntakeType;
import backend.mulkkam.cup.domain.vo.CupAmount;
import backend.mulkkam.cup.domain.vo.CupNickname;
import backend.mulkkam.cup.domain.vo.CupRank;
import backend.mulkkam.cup.dto.CreateCupRanked;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "컵 생성 요청")
public record CreateCupWithoutRankRequest(
        @Schema(description = "컵 이름", example = "스타벅스 텀블러")
        String cupNickname,

        @Schema(description = "컵 용량", example = "500")
        Integer cupAmount,

        @Schema(description = "음료 종류", implementation = IntakeType.class)
        IntakeType intakeType,

        @Schema(description = "컵 이모지 id", example = "1")
        Long cupEmojiId
) {
    public CreateCupRanked toCreateCupRanked(
            CupRank cupRank,
            CupEmoji cupEmoji
    ) {
        return new CreateCupRanked(
                new CupNickname(cupNickname),
                new CupAmount(cupAmount),
                cupRank,
                intakeType,
                cupEmoji
        );
    }
}
