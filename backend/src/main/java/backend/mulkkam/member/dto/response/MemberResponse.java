package backend.mulkkam.member.dto.response;

import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.domain.vo.Gender;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "회원 정보 응답")
public record MemberResponse(
        @Schema(description = "회원 ID", example = "1")
        Long id,

        @Schema(description = "닉네임", example = "밍곰")
        String nickname,

        @Schema(description = "체중 (kg)", example = "50.0", nullable = true)
        Double weight,

        @Schema(description = "성별", example = "FEMALE", implementation = Gender.class, nullable = true)
        Gender gender,

        @Schema(description = "목표 음용량 (ml)", example = "2000", minimum = "1")
        int targetAmount
) {

    public MemberResponse(Member member) {
        this(
                member.getId(),
                member.getMemberNickname().value(),
                member.getPhysicalAttributes().getWeight(),
                member.getPhysicalAttributes().getGender(),
                member.getTargetAmount().value()
        );
    }
}
