package backend.mulkkam.admin.dto.response;

import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.domain.vo.Gender;
import backend.mulkkam.member.domain.vo.MemberRole;
import java.time.LocalDateTime;

public record GetAdminMemberListResponse(
        Long id,
        String nickname,
        Gender gender,
        Double weight,
        Integer targetAmount,
        MemberRole memberRole,
        LocalDateTime createdAt
) {
    public static GetAdminMemberListResponse from(Member member) {
        return new GetAdminMemberListResponse(
                member.getId(),
                member.getMemberNickname().value(),
                member.getPhysicalAttributes().getGender(),
                member.getPhysicalAttributes().getWeight(),
                member.getTargetAmount().value(),
                member.getMemberRole(),
                member.getCreatedAt()
        );
    }
}
