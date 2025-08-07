package backend.mulkkam.common.resolver;

import backend.mulkkam.member.domain.Member;

public record LoginMember(
        Long id
) {
    public LoginMember(Member member) {
        this(member.getId());
    }
}
