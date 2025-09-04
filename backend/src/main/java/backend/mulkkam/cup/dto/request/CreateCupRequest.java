package backend.mulkkam.cup.dto.request;

import backend.mulkkam.cup.domain.Cup;
import backend.mulkkam.cup.domain.CupEmoji;
import backend.mulkkam.cup.domain.IntakeType;
import backend.mulkkam.cup.domain.vo.CupAmount;
import backend.mulkkam.cup.domain.vo.CupNickname;
import backend.mulkkam.cup.domain.vo.CupRank;
import backend.mulkkam.member.domain.Member;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "컵 생성 요청")
public record CreateCupRequest(
        @Schema(description = "컵 이름", example = "스타벅스 텀블러")
        String cupNickname,
        @Schema(description = "컵 용량", example = "500")
        Integer cupAmount,
        @Schema(description = "음료 종류", implementation = IntakeType.class)
        String intakeType,
        @Schema(description = "컵 이모지 id", example = "1")
        Long cupEmojiId
) {

    public Cup toCup(
            Member member,
            CupRank cupRank,
            IntakeType intakeType,
            CupEmoji cupEmoji
    ) {
        return new Cup(
                member,
                new CupNickname(cupNickname),
                new CupAmount(cupAmount),
                cupRank,
                intakeType,
                cupEmoji
        );
    }
}
