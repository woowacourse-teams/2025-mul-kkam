package backend.mulkkam.common.dto;

import backend.mulkkam.member.domain.Member;

public record MemberDetails(
        Long id
) {
    public MemberDetails(Member member) {
        this(member.getId());
    }
}
