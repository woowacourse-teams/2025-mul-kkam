package backend.mulkkam.common.resolver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import backend.mulkkam.common.dto.MemberDetails;
import backend.mulkkam.member.domain.vo.MemberRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

@ExtendWith(MockitoExtension.class)
class MemberResolverTest {

    private final MemberResolver memberResolver = new MemberResolver();

    @DisplayName("resolveArgument 를 할 때")
    @Nested
    class ResolveArgument {

        @DisplayName("request attribute에 memberDetails가 있으면 그대로 반환한다")
        @Test
        void success_whenMemberDetailsExists() {
            // given
            long memberId = 1L;
            MemberDetails memberDetails = new MemberDetails(memberId, MemberRole.MEMBER);

            MockHttpServletRequest servletRequest = new MockHttpServletRequest();
            servletRequest.setAttribute("memberDetails", memberDetails);
            NativeWebRequest webRequest = new ServletWebRequest(servletRequest);

            // when
            MemberDetails result = memberResolver.resolveArgument(
                    mock(MethodParameter.class),
                    mock(ModelAndViewContainer.class),
                    webRequest,
                    mock(WebDataBinderFactory.class)
            );

            // then
            assertThat(result).isEqualTo(memberDetails);
        }

        @DisplayName("request attribute에 memberDetails가 없으면 null을 반환한다")
        @Test
        void success_whenMemberDetailsDoesNotExist() {
            // given
            MockHttpServletRequest servletRequest = new MockHttpServletRequest();
            NativeWebRequest webRequest = new ServletWebRequest(servletRequest);

            // when
            MemberDetails result = memberResolver.resolveArgument(
                    mock(MethodParameter.class),
                    mock(ModelAndViewContainer.class),
                    webRequest,
                    mock(WebDataBinderFactory.class)
            );

            // then
            assertThat(result).isNull();
        }
    }
}

