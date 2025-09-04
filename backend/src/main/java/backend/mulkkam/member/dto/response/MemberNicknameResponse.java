package backend.mulkkam.member.dto.response;

import backend.mulkkam.member.domain.vo.MemberNickname;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "닉네임 조회 응답")
public record MemberNicknameResponse(
        @Schema(description = "회원 닉네임", example = "밍곰")
        String memberNickname
) {

    public MemberNicknameResponse(MemberNickname memberNickname) {
        this(memberNickname.value());
    }
}
