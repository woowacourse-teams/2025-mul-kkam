package backend.mulkkam.cup.dto.request;

import backend.mulkkam.cup.domain.Cup;
import backend.mulkkam.cup.domain.IntakeType;
import backend.mulkkam.cup.domain.vo.CupAmount;
import backend.mulkkam.cup.domain.vo.CupNickname;
import backend.mulkkam.cup.domain.vo.CupRank;
import backend.mulkkam.member.domain.Member;

public record CupRegisterRequest(
        String cupNickname,
        Integer cupAmount,
        String intakeType,
        String emoji
) {

    public Cup toCup(
            Member member,
            CupRank cupRank,
            IntakeType intakeType
    ) {
        return new Cup(
                member,
                new CupNickname(cupNickname),
                new CupAmount(cupAmount),
                cupRank,
                intakeType,
                emoji
        );
    }
}
