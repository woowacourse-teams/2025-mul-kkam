package backend.mulkkam.common.resolver;

import backend.mulkkam.auth.domain.OauthAccount;
import backend.mulkkam.auth.repository.OauthAccountRepository;
import backend.mulkkam.member.domain.Member;
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
public class MemberResolver implements HandlerMethodArgumentResolver {

    private final OauthAccountRepository oauthAccountRepository;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(Member.class);
    }

    @Override
    public Member resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory
    ) {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);

        Long accountId = (Long) request.getAttribute("subject");

        OauthAccount oauthAccount = oauthAccountRepository.findByIdWithMember(accountId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다"));

        return oauthAccount.getMember();
    }
}
