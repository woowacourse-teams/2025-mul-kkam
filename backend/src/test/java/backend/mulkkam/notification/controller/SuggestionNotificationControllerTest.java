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
import backend.mulkkam.intake.domain.IntakeHistory;
import backend.mulkkam.intake.repository.IntakeHistoryRepository;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.notification.domain.Notification;
import backend.mulkkam.notification.domain.SuggestionNotification;
import backend.mulkkam.notification.repository.SuggestionNotificationRepository;
import backend.mulkkam.support.ControllerTest;
import backend.mulkkam.support.MemberFixtureBuilder;
import backend.mulkkam.support.NotificationFixtureBuilder;
import backend.mulkkam.support.SuggestionNotificationFixtureBuilder;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;

@SpringBootTest
@AutoConfigureMockMvc
class SuggestionNotificationControllerTest extends ControllerTest {

    @Autowired
    private OauthJwtTokenHandler oauthJwtTokenHandler;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OauthAccountRepository oauthAccountRepository;

    private String token;

    private Member member;
    @Autowired
    private SuggestionNotificationRepository suggestionNotificationRepository;
    @Autowired
    private IntakeHistoryRepository intakeHistoryRepository;

    @BeforeEach
    void setUp() {
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

        private SuggestionNotification suggestionNotification;
        private Long savedSuggestionNotificationId;

        @BeforeEach
        void setUp() {
            Notification notification = NotificationFixtureBuilder
                    .withMember(member)
                    .build();
            suggestionNotification = SuggestionNotificationFixtureBuilder
                    .withNotification(notification)
                    .build();
            savedSuggestionNotificationId = suggestionNotificationRepository.save(suggestionNotification).getId();
        }

        @DisplayName("올바른 제안 알림 ID로 요청 시 적용에 성공한다")
        @Test
        void success_whenGivenValidSuggestionNotificationId() throws Exception {
            // when & then
            mockMvc.perform(post("/suggestion-notifications/approval/" + savedSuggestionNotificationId)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            List<IntakeHistory> intakeHistories = intakeHistoryRepository.findAllByMember(member);
            SuggestionNotification savedSuggestionNotification = suggestionNotificationRepository.findByIdAndNotificationMemberId(
                    savedSuggestionNotificationId,
                    member.getId()).get();
            assertSoftly(softly -> {
                softly.assertThat(intakeHistories.size()).isEqualTo(1);
                softly.assertThat(intakeHistories.getFirst().getTargetAmount().value())
                        .isEqualTo(2_800);
                softly.assertThat(savedSuggestionNotification.isApplyTargetAmount()).isTrue();
            });
        }

        @DisplayName("알림을 N개 받을 시 그에 맞춰 음용량이 변경된다.")
        @Test
        void success_whenGivenContinuousValidSuggestionNotificationId() throws Exception {
            // given
            Notification notification = NotificationFixtureBuilder
                    .withMember(member)
                    .build();
            suggestionNotification = SuggestionNotificationFixtureBuilder
                    .withNotification(notification)
                    .recommendedTargetAmount(1500)
                    .build();
            SuggestionNotification savedSuggestionNotification2 = suggestionNotificationRepository.save(
                    suggestionNotification);

            // when & then
            mockMvc.perform(post("/suggestion-notifications/approval/" + savedSuggestionNotificationId)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();
            mockMvc.perform(post("/suggestion-notifications/approval/" + savedSuggestionNotification2.getId())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            List<IntakeHistory> intakeHistories = intakeHistoryRepository.findAllByMember(member);
            SuggestionNotification savedSuggestionNotification = suggestionNotificationRepository.findByIdAndNotificationMemberId(
                    savedSuggestionNotificationId,
                    member.getId()).get();
            assertSoftly(softly -> {
                softly.assertThat(intakeHistories.size()).isEqualTo(1);
                softly.assertThat(intakeHistories.getFirst().getTargetAmount().value())
                        .isEqualTo(4_300);
                softly.assertThat(savedSuggestionNotification.isApplyTargetAmount()).isTrue();
            });
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
