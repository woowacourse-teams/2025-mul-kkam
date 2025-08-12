package backend.mulkkam.member.dto.request;

import backend.mulkkam.member.domain.vo.MemberNickname;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "닉네임 수정 요청")
public record MemberNicknameModifyRequest(
        @Schema(description = "변경할 닉네임", example = "밍곰", minLength = 2, maxLength = 10)
        String memberNickname
) {

    public MemberNickname toMemberNickname() {
        return new MemberNickname(memberNickname);
    }
}
