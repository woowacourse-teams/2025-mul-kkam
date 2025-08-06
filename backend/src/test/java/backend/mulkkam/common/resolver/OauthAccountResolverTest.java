package backend.mulkkam.common.resolver;

import backend.mulkkam.auth.domain.OauthAccount;
import backend.mulkkam.auth.domain.OauthProvider;
import backend.mulkkam.auth.infrastructure.OauthJwtTokenHandler;
import backend.mulkkam.auth.repository.OauthAccountRepository;
import backend.mulkkam.common.filter.AuthenticationHeaderHandler;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class OauthAccountResolverTest {

    @Mock
    OauthJwtTokenHandler oauthJwtTokenHandler;

    @Mock
    AuthenticationHeaderHandler authenticationHeaderHandler;

    @Mock
    OauthAccountRepository oauthAccountRepository;

    @InjectMocks
    OauthAccountResolver oauthAccountResolver;

    @DisplayName("resolveArgument 를 할 때")
    @Nested
    class ResolveArgument {

        @DisplayName("토큰을 추출해 성공적으로 OauthAccount 를 반환한다")
        @Test
        void success_validToken() throws Exception {
            // given
            String token = "test-token";

            MockHttpServletRequest servletRequest = new MockHttpServletRequest();
            servletRequest.addHeader("Authorization", "Bearer " + token);
            NativeWebRequest webRequest = new ServletWebRequest(servletRequest);

            long oauthAccountId = 1L;
            OauthAccount oauthAccount = new OauthAccount(oauthAccountId, "tempId", OauthProvider.KAKAO);
            given(authenticationHeaderHandler.extractToken(servletRequest)).willReturn(token);
            given(oauthJwtTokenHandler.getSubject(token)).willReturn(oauthAccount.getId());
            given(oauthAccountRepository.findById(oauthAccount.getId())).willReturn(Optional.of(oauthAccount));

            // when
            OauthAccount result = oauthAccountResolver.resolveArgument(
                    mock(MethodParameter.class),
                    mock(ModelAndViewContainer.class),
                    webRequest,
                    mock(WebDataBinderFactory.class)
            );

            // then
            assertSoftly(softAssertions -> {
                assertThat(result).isInstanceOf(OauthAccount.class);
                assertThat(result.getId()).isEqualTo(oauthAccountId);
            });
        }
    }
}
