package backend.mulkkam.friend.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import backend.mulkkam.auth.domain.OauthAccount;
import backend.mulkkam.auth.domain.OauthProvider;
import backend.mulkkam.auth.infrastructure.OauthJwtTokenHandler;
import backend.mulkkam.auth.repository.OauthAccountRepository;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.domain.vo.MemberNickname;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.support.controller.ControllerTest;
import backend.mulkkam.support.fixture.member.MemberFixtureBuilder;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;

class FriendControllerTest extends ControllerTest {

    @Autowired
    private OauthJwtTokenHandler oauthJwtTokenHandler;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OauthAccountRepository oauthAccountRepository;

    private final AtomicLong oauthIdCounter = new AtomicLong(1);

    record AuthenticatedMember(Member member, OauthAccount oauthAccount, String token) {}

    private AuthenticatedMember createAuthenticatedMember(String nickname) {
        Member member = memberRepository.save(
                MemberFixtureBuilder.builder()
                        .memberNickname(new MemberNickname(nickname))
                        .build()
        );
        OauthAccount oauthAccount = oauthAccountRepository.save(
                new OauthAccount(member, "oauthId" + oauthIdCounter.getAndIncrement(), OauthProvider.KAKAO)
        );
        String token = oauthJwtTokenHandler.createAccessToken(oauthAccount, nickname);
        return new AuthenticatedMember(member, oauthAccount, token);
    }

    @DisplayName("친구에게 물풍선을 보낼 때")
    @Nested
    class CreateReminder {

        @DisplayName("유효하지 않은 요청 데이터인 경우 예외가 발생한다")
        @Test
        void fail_returns_400_when_request_is_invalid() throws Exception {
            // given
            AuthenticatedMember requester = createAuthenticatedMember("요청자");
            String invalidRequest = "{\"memberId\": null}";

            // when & then
            mockMvc.perform(post("/friends/reminder")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + requester.token())
                            .contentType(APPLICATION_JSON)
                            .content(invalidRequest))
                    .andExpect(status().isBadRequest());
        }
    }
}
