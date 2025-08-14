package backend.mulkkam.common.dto;

import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.domain.vo.MemberNickname;

public record MemberDetails(
        Long id,
        MemberNickname nickname
) {
    public MemberDetails(Member member) {
        this(member.getId(), member.getMemberNickname());
    }
}
