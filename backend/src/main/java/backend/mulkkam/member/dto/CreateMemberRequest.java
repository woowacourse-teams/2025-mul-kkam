package backend.mulkkam.member.dto;

import backend.mulkkam.cup.dto.request.CreateCupRequest;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.domain.vo.Gender;
import backend.mulkkam.member.domain.vo.MemberNickname;
import backend.mulkkam.member.domain.vo.PhysicalAttributes;
import backend.mulkkam.member.domain.vo.TargetAmount;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import java.util.List;

@Schema(description = "온보딩 정보 생성 요청")
public record CreateMemberRequest(

        @Schema(description = "닉네임", example = "밍곰", minLength = 2, maxLength = 10)
        String memberNickname,

        @Schema(description = "체중 (kg)", example = "50.0")
        Double weight,

        @Schema(description = "성별", example = "FEMALE", implementation = Gender.class)
        Gender gender,

        @Schema(description = "목표 음용량 (ml)", example = "2000", minimum = "1")
        int targetIntakeAmount,

        @Schema(description = "마케팅 알림 수신 동의 여부", example = "true")
        boolean isMarketingNotificationAgreed,

        @Schema(description = "야간 알림 수신 동의 여부", example = "false")
        boolean isNightNotificationAgreed,

        @Schema(description = "컵 생성 데이터 리스트")
        List<@Valid CreateCupRequest> createCupRequests
) {
    public Member toMember() {
        return new Member(
                new MemberNickname(memberNickname),
                new PhysicalAttributes(gender, weight),
                new TargetAmount(targetIntakeAmount),
                isMarketingNotificationAgreed,
                isMarketingNotificationAgreed,
                memberNickname
        );
    }
}
