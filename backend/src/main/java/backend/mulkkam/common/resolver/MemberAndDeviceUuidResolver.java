package backend.mulkkam.common.resolver;

import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_OAUTH_ACCOUNT;

import backend.mulkkam.auth.domain.OauthAccount;
import backend.mulkkam.auth.repository.OauthAccountRepository;
import backend.mulkkam.common.dto.MemberAndDeviceUuidDetails;
import backend.mulkkam.common.exception.CommonException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@RequiredArgsConstructor
@Component
public class MemberAndDeviceUuidResolver implements HandlerMethodArgumentResolver {

    private final OauthAccountRepository oauthAccountRepository;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(MemberAndDeviceUuidDetails.class);
    }

    @Override
    public MemberAndDeviceUuidDetails resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);

        Long accountId = (Long) request.getAttribute("account_id");
        OauthAccount account = oauthAccountRepository.findByIdWithMember(accountId)
                .orElseThrow(() -> new CommonException(NOT_FOUND_OAUTH_ACCOUNT));
        String deviceUuid = (String) request.getAttribute("device_uuid");
        return new MemberAndDeviceUuidDetails(account.getMember().getId(), deviceUuid);
    }
}
