package backend.mulkkam.admin.dto.response;

import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.domain.vo.Gender;
import backend.mulkkam.member.domain.vo.MemberRole;
import java.time.LocalDateTime;

public record GetAdminMemberDetailResponse(
        Long id,
        String nickname,
        Gender gender,
        Double weight,
        Integer targetAmount,
        boolean isMarketingNotificationAgreed,
        boolean isNightNotificationAgreed,
        boolean isReminderEnabled,
        MemberRole memberRole,
        LocalDateTime createdAt
) {
    public static GetAdminMemberDetailResponse from(Member member) {
        return new GetAdminMemberDetailResponse(
                member.getId(),
                member.getMemberNickname().value(),
                member.getPhysicalAttributes().getGender(),
                member.getPhysicalAttributes().getWeight(),
                member.getTargetAmount().value(),
                member.isMarketingNotificationAgreed(),
                member.isNightNotificationAgreed(),
                member.isReminderEnabled(),
                member.getMemberRole(),
                member.getCreatedAt()
        );
    }
}
