package backend.mulkkam.common.resolver;

import static backend.mulkkam.auth.domain.OauthProvider.KAKAO;
import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_OAUTH_ACCOUNT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import backend.mulkkam.auth.domain.OauthAccount;
import backend.mulkkam.auth.repository.OauthAccountRepository;
import backend.mulkkam.common.dto.MemberDetails;
import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.support.fixture.MemberFixtureBuilder;
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

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class MemberResolverTest {

    @Mock
    private OauthAccountRepository oauthAccountRepository;

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
            long accountId = 1L;
            long memberId = 1L;
            Member member = MemberFixtureBuilder.builder().buildWithId(memberId);
            OauthAccount account = new OauthAccount(accountId, member, "oauthId", KAKAO);

            MockHttpServletRequest servletRequest = new MockHttpServletRequest();
            servletRequest.setAttribute("account_id", accountId);
            servletRequest.addHeader("Authorization", "Bearer " + token);
            NativeWebRequest webRequest = new ServletWebRequest(servletRequest);

            when(oauthAccountRepository.findByIdWithMember(accountId)).thenReturn(Optional.of(account));

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

        @DisplayName("토큰에 멤버 정보가 없는 경우 예외가 발생한다.")
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
            }).isInstanceOf(CommonException.class).hasMessage(NOT_FOUND_OAUTH_ACCOUNT.name());
        }
    }
}

