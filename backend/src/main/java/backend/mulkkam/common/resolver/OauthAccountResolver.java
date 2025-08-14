package backend.mulkkam.common.resolver;

import backend.mulkkam.auth.repository.OauthAccountRepository;
import backend.mulkkam.common.dto.OauthAccountDetails;
import backend.mulkkam.common.exception.CommonException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_MEMBER;

@Component
@RequiredArgsConstructor
public class OauthAccountResolver implements HandlerMethodArgumentResolver {

    private final OauthAccountRepository oauthAccountRepository;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(OauthAccountDetails.class);
    }

    @Override
    public OauthAccountDetails resolveArgument(MethodParameter parameter,
                                               ModelAndViewContainer mavContainer,
                                               NativeWebRequest webRequest,
                                               WebDataBinderFactory binderFactory
    ) {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);

        Long accountId = (Long) request.getAttribute("subject");

        oauthAccountRepository.findById(accountId)
                .orElseThrow(() -> new CommonException(NOT_FOUND_MEMBER));

        return new OauthAccountDetails(accountId);
    }
}
