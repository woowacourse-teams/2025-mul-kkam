package backend.mulkkam.admin.dto.request;

import backend.mulkkam.member.domain.vo.Gender;
import backend.mulkkam.member.domain.vo.MemberRole;
import io.swagger.v3.oas.annotations.media.Schema;

public record UpdateAdminMemberRequest(
        @Schema(description = "닉네임 (2~10자)", example = "홍길동")
        String nickname,

        @Schema(description = "성별", example = "MALE")
        Gender gender,

        @Schema(description = "체중 (kg)", example = "70.0")
        Double weight,

        @Schema(description = "목표 섭취량 (200~5000ml)", example = "2000")
        int targetAmount,

        @Schema(description = "마케팅 알림 동의")
        boolean isMarketingNotificationAgreed,

        @Schema(description = "야간 알림 동의")
        boolean isNightNotificationAgreed,

        @Schema(description = "리마인더 활성화")
        boolean isReminderEnabled,

        @Schema(description = "회원 권한", example = "MEMBER")
        MemberRole memberRole
) {
}
