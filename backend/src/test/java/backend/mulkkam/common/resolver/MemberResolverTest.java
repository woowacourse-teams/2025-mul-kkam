package backend.mulkkam.common.resolver;

import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_MEMBER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.Mockito.mock;

import backend.mulkkam.common.dto.MemberDetails;
import backend.mulkkam.common.exception.CommonException;
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
class MemberResolverTest {

    @InjectMocks
    private MemberResolver memberResolver;

    @DisplayName("resolveArgument 를 할 때")
    @Nested
    class ResolveArgument {

        @DisplayName("토큰을 추출해 성공적으로 Member 를 반환한다")
        @Test
        void success_validToken() {
            // given
            String token = "test-token";
            long memberId = 1L;

            MockHttpServletRequest servletRequest = new MockHttpServletRequest();
            servletRequest.setAttribute("member_id", memberId);
            servletRequest.addHeader("Authorization", "Bearer " + token);
            NativeWebRequest webRequest = new ServletWebRequest(servletRequest);

            // when
            MemberDetails result = memberResolver.resolveArgument(
                    mock(MethodParameter.class),
                    mock(ModelAndViewContainer.class),
                    webRequest,
                    mock(WebDataBinderFactory.class)
            );

            // then
            assertSoftly(softAssertions -> {
                assertThat(result).isInstanceOf(MemberDetails.class);
                assertThat(result.id()).isEqualTo(memberId);
            });
        }

        @DisplayName("토큰을 추출해 성공적으로 Member 를 반환한다")
        @Test
        void error_didNotOnboarded() {
            // given
            String token = "test-token";

            MockHttpServletRequest servletRequest = new MockHttpServletRequest();
            servletRequest.addHeader("Authorization", "Bearer " + token);
            NativeWebRequest webRequest = new ServletWebRequest(servletRequest);

            // when & then
            assertThatThrownBy(() -> {
                memberResolver.resolveArgument(
                        mock(MethodParameter.class),
                        mock(ModelAndViewContainer.class),
                        webRequest,
                        mock(WebDataBinderFactory.class)
                );
            }).isInstanceOf(CommonException.class).hasMessage(NOT_FOUND_MEMBER.name());
        }
    }
}

