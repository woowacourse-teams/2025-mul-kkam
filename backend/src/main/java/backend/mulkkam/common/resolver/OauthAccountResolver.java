package backend.mulkkam.common.resolver;

import backend.mulkkam.auth.domain.OauthAccount;
import backend.mulkkam.auth.infrastructure.OauthJwtTokenHandler;
import backend.mulkkam.auth.repository.OauthAccountRepository;
import backend.mulkkam.common.filter.AuthenticationHeaderHandler;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
public class OauthAccountResolver implements HandlerMethodArgumentResolver {

    private final AuthenticationHeaderHandler authenticationHeaderHandler;
    private final OauthJwtTokenHandler oauthJwtTokenHandler;
    private final OauthAccountRepository oauthAccountRepository;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(OauthAccount.class);
    }

    @Override
    public OauthAccount resolveArgument(MethodParameter parameter,
                                        ModelAndViewContainer mavContainer,
                                        NativeWebRequest webRequest,
                                        WebDataBinderFactory binderFactory
    ) {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);

        String token = authenticationHeaderHandler.extractToken(request);
        Long oauthAccountId = oauthJwtTokenHandler.getSubject(token);

        // TODO: 추후 에러 코드로 반영
        return oauthAccountRepository.findById(oauthAccountId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다"));
    }
}
