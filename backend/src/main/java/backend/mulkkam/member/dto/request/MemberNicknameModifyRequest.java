package backend.mulkkam.member.dto.request;

import backend.mulkkam.member.domain.vo.MemberNickname;

public record MemberNicknameModifyRequest(String memberNickname) {

    public MemberNickname toMemberNickname() {
        return new MemberNickname(memberNickname);
    }
}
