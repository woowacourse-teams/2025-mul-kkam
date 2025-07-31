package backend.mulkkam.cup.dto.request;

import backend.mulkkam.cup.domain.Cup;
import backend.mulkkam.cup.domain.IntakeType;
import backend.mulkkam.cup.domain.vo.CupAmount;
import backend.mulkkam.cup.domain.vo.CupNickname;
import backend.mulkkam.cup.domain.vo.CupRank;
import backend.mulkkam.member.domain.Member;

public record RegisterCupRequest(
        String cupNickname,
        Integer cupAmount,
        IntakeType intakeType
) {

    public Cup toCup(
            Member member,
            CupRank cupRank
    ) {
        return new Cup(
                member,
                new CupNickname(cupNickname),
                new CupAmount(cupAmount),
                cupRank,
                intakeType
        );
    }
}
