package backend.mulkkam.common.auth;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import backend.mulkkam.auth.domain.OauthAccount;
import backend.mulkkam.auth.domain.OauthProvider;
import backend.mulkkam.auth.infrastructure.OauthJwtTokenHandler;
import backend.mulkkam.auth.repository.OauthAccountRepository;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.support.controller.ControllerTest;
import backend.mulkkam.support.fixture.member.MemberFixtureBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;

@DisplayName("인증/인가")
class AuthenticationTest extends ControllerTest {

    @Autowired
    private OauthJwtTokenHandler oauthJwtTokenHandler;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OauthAccountRepository oauthAccountRepository;

    private String createMemberToken() {
        Member member = memberRepository.save(MemberFixtureBuilder.builder().build());
        OauthAccount oauthAccount = oauthAccountRepository.save(
                new OauthAccount(member, "oauthId", OauthProvider.KAKAO)
        );
        return oauthJwtTokenHandler.createAccessToken(oauthAccount, "deviceUuid");
    }

    private String createAccountOnlyToken() {
        OauthAccount oauthAccount = oauthAccountRepository.save(
                new OauthAccount("oauthIdWithoutMember", OauthProvider.KAKAO)
        );
        return oauthJwtTokenHandler.createAccessToken(oauthAccount, "deviceUuid");
    }

    @DisplayName("인증이 필요한 엔드포인트")
    @Nested
    class RequireAuthentication {

        @DisplayName("토큰이 없으면 401 응답")
        @Test
        void fail_returns_401_when_no_token() throws Exception {
            mockMvc.perform(get("/members"))
                    .andExpect(status().isUnauthorized());
        }

        @DisplayName("유효하지 않은 토큰이면 401 응답")
        @Test
        void fail_returns_401_when_invalid_token() throws Exception {
            mockMvc.perform(get("/members")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer invalid.token.here"))
                    .andExpect(status().isUnauthorized());
        }

        @DisplayName("유효한 멤버 토큰이면 정상 처리")
        @Test
        void success_request_is_processed_with_valid_token() throws Exception {
            String token = createMemberToken();

            mockMvc.perform(get("/members")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isOk());
        }
    }

    @DisplayName("멤버 권한이 필요한 엔드포인트")
    @Nested
    class RequireMemberLevel {

        @DisplayName("온보딩 미완료 계정(Account만 있음)으로 접근하면 403 응답")
        @Test
        void fail_returns_403_when_onboarding_incomplete() throws Exception {
            String token = createAccountOnlyToken();

            mockMvc.perform(get("/members")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isForbidden());
        }
    }

    @DisplayName("인증이 필요 없는 엔드포인트")
    @Nested
    class NoAuthenticationRequired {

        @DisplayName("인증 없이도 요청은 컨트롤러까지 도달")
        @Test
        void success_request_reaches_controller_without_auth() throws Exception {
            // 카카오 API 호출 실패로 500이 나오지만, 인증 필터는 통과한 것
            mockMvc.perform(post("/auth/kakao")
                            .contentType("application/json")
                            .content("{\"kakaoAccessToken\": \"test\", \"deviceUuid\": \"uuid\"}"))
                    .andExpect(status().is5xxServerError());
        }
    }
}
