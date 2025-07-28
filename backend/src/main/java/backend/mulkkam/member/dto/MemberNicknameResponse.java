package backend.mulkkam.member.dto;

import backend.mulkkam.member.domain.Member;

// TODO 2025. 7. 28. 22:40: 디렉토리 분리 response, request
public record MemberNicknameResponse(String memberNickname) {

    public MemberNicknameResponse(Member member) {
        this(member.getMemberNickname().value());
    }
}
