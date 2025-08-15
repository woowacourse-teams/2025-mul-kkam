package backend.mulkkam.common.resolver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.Mockito.mock;

import backend.mulkkam.common.dto.MemberDetails;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.domain.vo.Gender;
import backend.mulkkam.member.domain.vo.MemberNickname;
import backend.mulkkam.member.domain.vo.PhysicalAttributes;
import backend.mulkkam.member.domain.vo.TargetAmount;
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
    MemberResolver memberResolver;

    @DisplayName("resolveArgument 를 할 때")
    @Nested
    class ResolveArgument {

        @DisplayName("토큰을 추출해 성공적으로 Member 를 반환한다")
        @Test
        void success_validToken() throws Exception {
            // given
            String token = "test-token";
            long memberId = 1L;

            MockHttpServletRequest servletRequest = new MockHttpServletRequest();
            servletRequest.setAttribute("subject", memberId);
            servletRequest.addHeader("Authorization", "Bearer " + token);
            NativeWebRequest webRequest = new ServletWebRequest(servletRequest);

            Member member = new Member(
                    memberId,
                    new MemberNickname("히로"),
                    new PhysicalAttributes(Gender.FEMALE, 70.0),
                    new TargetAmount(1_000),
                    true,
                    false
            );

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
    }
}

