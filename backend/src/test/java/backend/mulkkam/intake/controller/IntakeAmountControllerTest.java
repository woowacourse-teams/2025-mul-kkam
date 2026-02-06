package backend.mulkkam.intake.controller;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import backend.mulkkam.auth.domain.OauthAccount;
import backend.mulkkam.auth.domain.OauthProvider;
import backend.mulkkam.auth.infrastructure.OauthJwtTokenHandler;
import backend.mulkkam.auth.repository.OauthAccountRepository;
import backend.mulkkam.intake.dto.SuggestionIntakeAmountResponse;
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

class IntakeAmountControllerTest extends ControllerTest {

    @Autowired
    private OauthJwtTokenHandler oauthJwtTokenHandler;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OauthAccountRepository oauthAccountRepository;

    private final AtomicLong oauthIdCounter = new AtomicLong(1);

    record AuthenticatedMember(Member member, OauthAccount oauthAccount, String token) {}

    private AuthenticatedMember createAuthenticatedMember() {
        return createAuthenticatedMember(70.0, 1500);
    }

    private AuthenticatedMember createAuthenticatedMember(Double weight, Integer targetAmount) {
        Member member = memberRepository.save(MemberFixtureBuilder
                .builder()
                .memberNickname(new MemberNickname("테스터" + oauthIdCounter.get()))
                .weight(weight)
                .targetAmount(targetAmount)
                .build());

        OauthAccount oauthAccount = oauthAccountRepository.save(
                new OauthAccount(member, "testId" + oauthIdCounter.getAndIncrement(), OauthProvider.KAKAO)
        );

        String deviceUuid = "deviceUuid";
        String token = oauthJwtTokenHandler.createAccessToken(oauthAccount, deviceUuid);

        return new AuthenticatedMember(member, oauthAccount, token);
    }

    @DisplayName("목표 음용량을 추천받을 때에")
    @Nested
    class GetRecommended {

        @DisplayName("입력받은 몸무게가 있으면 해당 값 * 30을 추천한다")
        @Test
        void success_recommends_weight_times_30() throws Exception {
            // given
            AuthenticatedMember auth = createAuthenticatedMember();

            // when
            String json = mockMvc.perform(get("/intake/amount/recommended")
                            .param("weight", "70")
                            .param("GENDER", "FEMALE")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + auth.token()))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            SuggestionIntakeAmountResponse actual = objectMapper.readValue(json,
                    SuggestionIntakeAmountResponse.class);

            // then
            assertSoftly(softly -> {
                softly.assertThat(actual.amount()).isEqualTo(2100);
            });
        }

        @DisplayName("입력받은 몸무게가 null 이라면 기본값 60 * 30을 추천한다")
        @Test
        void success_recommends_default_when_weight_is_null() throws Exception {
            // given
            AuthenticatedMember auth = createAuthenticatedMember();

            // when
            String json = mockMvc.perform(get("/intake/amount/target/recommended")
                            .param("weight", (String) null)
                            .param("GENDER", "FEMALE")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + auth.token()))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            SuggestionIntakeAmountResponse actual = objectMapper.readValue(json,
                    SuggestionIntakeAmountResponse.class);

            // then
            assertSoftly(softly -> {
                softly.assertThat(actual.amount()).isEqualTo(1800);
            });
        }
    }
}
