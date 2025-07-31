package backend.mulkkam.member.dto.response;

import backend.mulkkam.member.domain.vo.MemberNickname;

public record MemberNicknameResponse(String memberNickname) {

    public MemberNicknameResponse(MemberNickname memberNickname) {
        this(memberNickname.value());
    }
}
