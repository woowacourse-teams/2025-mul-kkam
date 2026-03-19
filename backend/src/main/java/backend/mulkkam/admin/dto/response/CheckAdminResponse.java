package backend.mulkkam.admin.dto.response;

import backend.mulkkam.common.dto.MemberDetails;
import backend.mulkkam.member.domain.vo.MemberRole;

public record CheckAdminResponse(
        boolean isAdmin,
        MemberRole memberRole,
        boolean finishedOnboarding
) {
    public static CheckAdminResponse from(MemberDetails memberDetails) {
        MemberRole role = memberDetails.memberRole();
        return new CheckAdminResponse(
                memberDetails.isAdmin(),
                role,
                role != MemberRole.NONE
        );
    }
}
