package backend.mulkkam.common.dto;

import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.domain.vo.MemberRole;

public record MemberDetails(
        Long id,
        MemberRole memberRole
) {
    public MemberDetails(Member member) {
        this(member.getId(), member.getMemberRole());
    }

    public boolean isAdmin() {
        return memberRole == MemberRole.ADMIN;
    }
}
