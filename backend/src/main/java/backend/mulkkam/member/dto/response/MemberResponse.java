package backend.mulkkam.member.dto.response;

import backend.mulkkam.member.domain.Member;

public record MemberResponse(
        Long id,
        String nickname,
        Double weight,
        String gender,
        int targetAmount
) {

    public MemberResponse(Member member) {
        this(
                member.getId(),
                member.getMemberNickname().value(),
                member.getPhysicalAttributes().getWeight(),
                member.getPhysicalAttributes().getGender().name(),
                member.getTargetAmount().value()
        );
    }
}
