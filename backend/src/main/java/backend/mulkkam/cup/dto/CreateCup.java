package backend.mulkkam.cup.dto;

import backend.mulkkam.cup.domain.Cup;
import backend.mulkkam.cup.domain.CupEmoji;
import backend.mulkkam.cup.domain.IntakeType;
import backend.mulkkam.cup.domain.vo.CupAmount;
import backend.mulkkam.cup.domain.vo.CupNickname;
import backend.mulkkam.cup.domain.vo.CupRank;
import backend.mulkkam.member.domain.Member;


public record CreateCup(
        CupNickname cupNickname,
        CupAmount cupAmount,
        CupRank cupRank,
        IntakeType intakeType,
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
