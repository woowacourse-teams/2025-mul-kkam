package backend.mulkkam.member.dto.response;

import backend.mulkkam.member.domain.Member;

public record MemberNicknameResponse(String memberNickname) {

    public MemberNicknameResponse(Member member) {
        this(member.getMemberNickname().value());
    }
}
