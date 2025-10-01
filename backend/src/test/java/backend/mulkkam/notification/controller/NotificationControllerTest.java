package backend.mulkkam.notification.controller;

import static backend.mulkkam.common.exception.errorCode.ForbiddenErrorCode.NOT_PERMITTED_FOR_NOTIFICATION;
import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_NOTIFICATION;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import backend.mulkkam.auth.domain.OauthAccount;
import backend.mulkkam.auth.domain.OauthProvider;
import backend.mulkkam.auth.infrastructure.OauthJwtTokenHandler;
import backend.mulkkam.auth.repository.OauthAccountRepository;
import backend.mulkkam.common.exception.FailureBody;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.domain.vo.MemberNickname;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.notification.domain.Notification;
import backend.mulkkam.notification.domain.NotificationType;
import backend.mulkkam.notification.dto.NotificationMessageTemplate.ReadNotificationsResponse;
import backend.mulkkam.notification.dto.response.GetUnreadNotificationsCountResponse;
import backend.mulkkam.notification.repository.NotificationRepository;
import backend.mulkkam.support.controller.ControllerTest;
import backend.mulkkam.support.fixture.member.MemberFixtureBuilder;
import backend.mulkkam.support.fixture.notification.NotificationFixtureBuilder;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;

public class NotificationControllerTest extends ControllerTest {

    @Autowired
    private OauthJwtTokenHandler oauthJwtTokenHandler;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OauthAccountRepository oauthAccountRepository;

    private Member savedMember;

    private String token;

    @BeforeEach
    void setUp() {
        Member member = MemberFixtureBuilder
                .builder().build();
        savedMember = memberRepository.save(member);

        OauthAccount oauthAccount = new OauthAccount(member, "testId", OauthProvider.KAKAO);
        oauthAccountRepository.save(oauthAccount);
        String deviceUuid = "deviceUuid";
        token = oauthJwtTokenHandler.createAccessToken(oauthAccount, deviceUuid);
    }

    private List<Notification> buildUnreadNotifications(Member member, List<LocalDate> createdDates) {
        return createdDates.stream()
                .map(date -> NotificationFixtureBuilder.withMember(member)
                        .createdAt(date)
                        .build())
                .toList();
    }

    @DisplayName("알림을 조회할 때")
    @Nested
    class ReadNotifications {

        @BeforeEach
        void setUp() {
            List<LocalDate> dates = IntStream.range(0, 10)
                    .mapToObj(i -> LocalDate.of(2025, 8, 15))
                    .toList();
            List<Notification> notifications = buildUnreadNotifications(savedMember, dates);
            notificationRepository.saveAll(notifications);
        }

        @DisplayName("유효한 요청이면 올바르게 반환한다")
        @Test
        void success_validInput() throws Exception {
            // when & then
            String json = mockMvc.perform(get("/notifications")
                            .param("lastId", "10")
                            .param("clientTime", "2025-08-15T13:11:00")
                            .param("size", "5")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            ReadNotificationsResponse actual = objectMapper.readValue(json, ReadNotificationsResponse.class);

            assertSoftly(softly -> {
                softly.assertThat(actual.nextCursor()).isEqualTo(5);
                softly.assertThat(actual.readNotificationResponses().size()).isEqualTo(5);
                softly.assertThat(actual.readNotificationResponses())
                        .allMatch(r -> r.id() >= 5L)
                        .allMatch(r -> !r.isRead());
            });
        }

        @DisplayName("lastId가 null이면 마지막 id부터 조회하여 올바르게 반환한다")
        @Test
        void success_lastIdIsNull() throws Exception {
            // when & then
            String json = mockMvc.perform(get("/notifications")
                            .param("clientTime", "2025-08-15T13:11:00")
                            .param("size", "5")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            ReadNotificationsResponse actual = objectMapper.readValue(json, ReadNotificationsResponse.class);

            assertSoftly(softly -> {
                softly.assertThat(actual.nextCursor()).isEqualTo(6);
                softly.assertThat(actual.readNotificationResponses().size()).isEqualTo(5);
                softly.assertThat(actual.readNotificationResponses())
                        .allMatch(r -> r.id() >= 6L)
                        .allMatch(r -> !r.isRead());
            });
        }
    }

    @DisplayName("읽지 않은 알림 수를 조회할 때")
    @Nested
    class GetUnreadNotificationsCount {

        @BeforeEach
        void setUp() {
            List<LocalDate> dates = IntStream.range(0, 7)
                    .mapToObj(i -> LocalDate.of(2025, 8, 15))
                    .toList();
            List<Notification> unreadNotifications = buildUnreadNotifications(savedMember, dates);
            notificationRepository.saveAll(unreadNotifications);

            List<Notification> readNotifications = List.of(
                    NotificationFixtureBuilder.withMember(savedMember)
                            .createdAt(LocalDate.of(2025, 8, 15))
                            .isRead(true)
                            .build(),
                    NotificationFixtureBuilder.withMember(savedMember)
                            .createdAt(LocalDate.of(2025, 8, 15))
                            .isRead(true)
                            .build(),
                    NotificationFixtureBuilder.withMember(savedMember)
                            .createdAt(LocalDate.of(2025, 8, 15))
                            .isRead(true)
                            .build()
            );
            notificationRepository.saveAll(readNotifications);
        }

        @DisplayName("유효한 요청이면 올바르게 반환한다")
        @Test
        void success_validMember() throws Exception {
            // when & then
            String json = mockMvc.perform(get("/notifications/unread-count?clientTime=2025-08-15T10:00:00")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            GetUnreadNotificationsCountResponse actual = objectMapper.readValue(json,
                    GetUnreadNotificationsCountResponse.class);

            assertThat(actual.count()).isEqualTo(7);
        }
    }

    @DisplayName("알림을 삭제할 때")
    @Nested
    class Delete {

        @DisplayName("유효한 요청인 경우 정상적으로 처리된다")
        @Test
        void success_withValidId() throws Exception {
            // given
            Notification notification = NotificationFixtureBuilder
                    .withMember(savedMember)
                    .notificationType(NotificationType.REMIND)
                    .build();
            notificationRepository.save(notification);

            // when
            mockMvc.perform(delete("/notifications/" + notification.getId())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isNoContent())
                    .andReturn().getResponse().getContentAsString();

            // then
            assertThat(notificationRepository.findById(notification.getId())).isEmpty();
        }

        @DisplayName("삭제 권한이 없는 사용자가 요청하는 경우 예외를 던진다")
        @Test
        void success_withForbiddenMember() throws Exception {
            // given
            Member anotherMember = MemberFixtureBuilder
                    .builder()
                    .memberNickname(new MemberNickname("칼리"))
                    .build();
            memberRepository.save(anotherMember);

            Notification notification = NotificationFixtureBuilder
                    .withMember(anotherMember)
                    .notificationType(NotificationType.REMIND)
                    .build();
            notificationRepository.save(notification);

            // when
            String json = mockMvc.perform(delete("/notifications/" + notification.getId())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isForbidden())
                    .andReturn().getResponse().getContentAsString();

            FailureBody actual = objectMapper.readValue(json, FailureBody.class);
            assertThat(actual.getCode()).isEqualTo(NOT_PERMITTED_FOR_NOTIFICATION.name());
        }

        @DisplayName("존재하지 않는 알림에 대한 삭제 요청인 경우 예외를 던진다")
        @Test
        void success_notExistingNotification() throws Exception {
            // when
            String json = mockMvc.perform(delete("/notifications/1")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isNotFound())
                    .andReturn().getResponse().getContentAsString();

            FailureBody actual = objectMapper.readValue(json, FailureBody.class);
            assertThat(actual.getCode()).isEqualTo(NOT_FOUND_NOTIFICATION.name());
        }
    }
}
