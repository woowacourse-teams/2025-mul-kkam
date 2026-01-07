package backend.mulkkam.common.auth;

import backend.mulkkam.common.dto.MemberDetails;
import backend.mulkkam.common.dto.OauthAccountDetails;
import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.common.exception.errorCode.ForbiddenErrorCode;
import backend.mulkkam.member.domain.vo.MemberRole;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AuthContext {

    private final HttpServletRequest request;

    public boolean requireMember() {
        Long memberId = (Long) request.getAttribute("member_id");
        MemberRole role = (MemberRole) request.getAttribute("member_role");
        if (memberId == null || role == MemberRole.NONE) {
            throw new CommonException(ForbiddenErrorCode.NOT_PERMITTED_FOR_ROLE);
        }
        request.setAttribute("member_details", new MemberDetails(memberId, role));
        return true;
    }

    public boolean requireAccount() {
        Long accountId = (Long) request.getAttribute("account_id");
        String deviceUuid = (String) request.getAttribute("device_uuid");
        if (accountId == null || deviceUuid == null || deviceUuid.isBlank()) {
            throw new CommonException(ForbiddenErrorCode.NOT_PERMITTED_FOR_ROLE);
        }
        request.setAttribute("oauth_account_details", new OauthAccountDetails(accountId, deviceUuid));
        return true;
    }

    public boolean requireAdmin() {
        Long memberId = (Long) request.getAttribute("member_id");
        MemberRole memberRole = (MemberRole) request.getAttribute("member_role");

        if (memberId == null || memberRole == null) {
            throw new CommonException(ForbiddenErrorCode.NOT_PERMITTED_FOR_ROLE);
        }
        if (!memberRole.isAdmin()) {
            throw new CommonException(ForbiddenErrorCode.NOT_PERMITTED_FOR_ROLE);
        }
        request.setAttribute("member_details", new MemberDetails(memberId, memberRole));
        return true;
    }
}
