package backend.mulkkam.member.dto;

import backend.mulkkam.intake.domain.vo.Amount;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.domain.vo.Gender;
import backend.mulkkam.member.domain.vo.MemberNickname;
import backend.mulkkam.member.domain.vo.PhysicalAttributes;

public record CreateMemberRequest(
        String memberNickname,
        Double weight,
        Gender gender,
        int targetIntakeAmount,
        boolean isMarketingNotificationAgreed,
        boolean isNightNotificationAgreed
) {
    public Member toMember() {
        return new Member(
                new MemberNickname(memberNickname),
                new PhysicalAttributes(gender, weight),
                new Amount(targetIntakeAmount),
                isMarketingNotificationAgreed,
                isMarketingNotificationAgreed
        );
    }
}
