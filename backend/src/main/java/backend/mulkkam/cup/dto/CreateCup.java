package backend.mulkkam.cup.dto;

import backend.mulkkam.cup.domain.Cup;
import backend.mulkkam.cup.domain.CupEmoji;
import backend.mulkkam.cup.domain.IntakeType;
import backend.mulkkam.cup.domain.vo.CupAmount;
import backend.mulkkam.cup.domain.vo.CupNickname;
import backend.mulkkam.cup.domain.vo.CupRank;
import backend.mulkkam.member.domain.Member;
import io.swagger.v3.oas.annotations.media.Schema;


public record CreateCup(
        @Schema(description = "컵 이름", example = "스타벅스 텀블러")
        CupNickname cupNickname,

        @Schema(description = "컵 용량", example = "500")
        CupAmount cupAmount,

        CupRank cupRank,

        @Schema(description = "음료 종류", implementation = IntakeType.class)
        IntakeType intakeType,

        @Schema(description = "컵 이모지 id", example = "1")
        CupEmoji cupEmoji
) {
    public Cup toCup(
            Member member
    ) {
        return new Cup(
                member,
                cupNickname,
                cupAmount,
                cupRank,
                intakeType,
                cupEmoji
        );
    }
}
