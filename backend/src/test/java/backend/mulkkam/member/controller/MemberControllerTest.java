package backend.mulkkam.member.controller;

import static backend.mulkkam.auth.domain.OauthProvider.KAKAO;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import backend.mulkkam.auth.domain.OauthAccount;
import backend.mulkkam.auth.infrastructure.OauthJwtTokenHandler;
import backend.mulkkam.auth.repository.OauthAccountRepository;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.dto.request.ModifyIsMarketingNotificationAgreedRequest;
import backend.mulkkam.member.dto.request.ModifyIsNightNotificationAgreedRequest;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.support.DatabaseCleaner;
import backend.mulkkam.support.MemberFixtureBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class MemberControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private OauthJwtTokenHandler oauthJwtTokenHandler;

    @Autowired
    private OauthAccountRepository oauthAccountRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    private Member member;

    private String token;

    @BeforeEach
    void setUp() {
        databaseCleaner.clean();

        member = MemberFixtureBuilder
                .builder()
                .isNightNotificationAgreed(true)
                .isMarketingNotificationAgreed(true)
                .build();
        memberRepository.save(member);
        OauthAccount oauthAccount = new OauthAccount(member, "test", KAKAO);
        oauthAccountRepository.save(oauthAccount);

        token = oauthJwtTokenHandler.createAccessToken(oauthAccount);
    }

    @DisplayName("멤버의 정보를 수정할 때에")
    @Nested
    class Modify {

        @DisplayName("야간 알림을 수정한다.")
        @Test
        void success_whenModifyIsNightNotificationAgreed() throws Exception {
            // given
            ModifyIsNightNotificationAgreedRequest modifyIsNightNotificationAgreedRequest = new ModifyIsNightNotificationAgreedRequest(
                    false);

            // when
            mockMvc.perform(patch("/members/notification/night")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(modifyIsNightNotificationAgreedRequest)))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();
            Member foundMember = memberRepository.findById(member.getId()).orElseThrow();

            // then
            assertSoftly(softly ->
                    softly.assertThat(foundMember.isNightNotificationAgreed()).isFalse()
            );
        }

        @DisplayName("야간 알림을 수정한다.")
        @Test
        void success_whenModifyIsMarketingNotificationAgreed() throws Exception {
            // given
            ModifyIsMarketingNotificationAgreedRequest modifyIsMarketingNotificationAgreedRequest = new ModifyIsMarketingNotificationAgreedRequest(
                    false);

            // when
            mockMvc.perform(patch("/members/notification/marketing")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(modifyIsMarketingNotificationAgreedRequest)))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();
            Member foundMember = memberRepository.findById(member.getId()).orElseThrow();

            // then
            assertSoftly(softly ->
                    softly.assertThat(foundMember.isMarketingNotificationAgreed()).isFalse()
            );
        }
    }
}
