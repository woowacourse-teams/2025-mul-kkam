package backend.mulkkam.common.resolver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.Mockito.mock;

import backend.mulkkam.common.dto.OauthAccountDetails;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

@ExtendWith(MockitoExtension.class)
class OauthAccountResolverTest {

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
            long oauthAccountId = 1L;

            MockHttpServletRequest servletRequest = new MockHttpServletRequest();
            servletRequest.addHeader("Authorization", "Bearer " + token);
            servletRequest.setAttribute("subject", oauthAccountId);
            NativeWebRequest webRequest = new ServletWebRequest(servletRequest);

            // when
            OauthAccountDetails result = oauthAccountResolver.resolveArgument(
                    mock(MethodParameter.class),
                    mock(ModelAndViewContainer.class),
                    webRequest,
                    mock(WebDataBinderFactory.class)
            );

            // then
            assertSoftly(softAssertions -> {
                assertThat(result).isInstanceOf(OauthAccountDetails.class);
                assertThat(result.id()).isEqualTo(oauthAccountId);
            });
        }
    }
}
