package backend.mulkkam.notification.controller;

import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_SUGGESTION_NOTIFICATION;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import backend.mulkkam.auth.domain.OauthAccount;
import backend.mulkkam.auth.domain.OauthProvider;
import backend.mulkkam.auth.infrastructure.OauthJwtTokenHandler;
import backend.mulkkam.auth.repository.OauthAccountRepository;
import backend.mulkkam.common.exception.FailureBody;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.notification.domain.Notification;
import backend.mulkkam.notification.repository.SuggestionNotificationRepository;
import backend.mulkkam.support.DatabaseCleaner;
import backend.mulkkam.support.MemberFixtureBuilder;
import backend.mulkkam.support.NotificationFixtureBuilder;
import backend.mulkkam.support.SuggestionNotificationFixtureBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class SuggestionNotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OauthJwtTokenHandler oauthJwtTokenHandler;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OauthAccountRepository oauthAccountRepository;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;

    private Member member;
    @Autowired
    private SuggestionNotificationRepository suggestionNotificationRepository;


    @BeforeEach
    void setUp() {
        databaseCleaner.clean();
        member = MemberFixtureBuilder
                .builder()
                .build();
        Member savedMember = memberRepository.save(member);
        OauthAccount oauthAccount = new OauthAccount(savedMember, "testId", OauthProvider.KAKAO);
        oauthAccountRepository.save(oauthAccount);
        token = oauthJwtTokenHandler.createAccessToken(oauthAccount);
    }

    @DisplayName("제안 음용량을 적용할 때")
    @Nested
    class ApplyTargetAmount {

        private Long savedSuggestionNotificationId;

        @BeforeEach
        void setUp() {
            Notification notification = NotificationFixtureBuilder.withMember(member).build();

            savedSuggestionNotificationId = suggestionNotificationRepository.save(
                    SuggestionNotificationFixtureBuilder.withNotification(notification).build()).getId();
        }

        @DisplayName("올바른 제안 알림 ID로 요청 시 적용에 성공한다")
        @Test
        void success_whenGivenValidSuggestionNotificationId() throws Exception {
            // when & then
            mockMvc.perform(post("/suggestion-notifications/approval/" + savedSuggestionNotificationId)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();
        }

        @DisplayName("존재하지 않는 제안 알림 ID로 요청 시 예외가 발생한다")
        @Test
        void error_whenNonExistingSuggestionNotificationId() throws Exception {
            // when & then
            String json = mockMvc.perform(post("/suggestion-notifications/approval/" + Long.MAX_VALUE)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isNotFound())
                    .andReturn().getResponse().getContentAsString();

            FailureBody actual = objectMapper.readValue(json, FailureBody.class);

            // then
            assertSoftly(softly -> {
                softly.assertThat(actual.getCode()).isEqualTo(NOT_FOUND_SUGGESTION_NOTIFICATION.name());
            });
        }
    }
}
