package backend.mulkkam.notification.controller;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import backend.mulkkam.auth.domain.OauthAccount;
import backend.mulkkam.auth.domain.OauthProvider;
import backend.mulkkam.auth.infrastructure.OauthJwtTokenHandler;
import backend.mulkkam.auth.repository.OauthAccountRepository;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.notification.domain.Notification;
import backend.mulkkam.notification.dto.GetNotificationsCountResponse;
import backend.mulkkam.notification.dto.ReadNotificationsResponse;
import backend.mulkkam.notification.repository.NotificationRepository;
import backend.mulkkam.support.DatabaseCleaner;
import backend.mulkkam.support.MemberFixtureBuilder;
import backend.mulkkam.support.NotificationFixtureBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.List;
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
public class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OauthJwtTokenHandler oauthJwtTokenHandler;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OauthAccountRepository oauthAccountRepository;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @Autowired
    private ObjectMapper objectMapper;

    private Member savedMember;

    private String token;

    @BeforeEach
    void setUp() {
        databaseCleaner.clean();

        Member member = MemberFixtureBuilder
                .builder().build();
        savedMember = memberRepository.save(member);

        OauthAccount oauthAccount = new OauthAccount(member, "testId", OauthProvider.KAKAO);
        oauthAccountRepository.save(oauthAccount);

        token = oauthJwtTokenHandler.createAccessToken(oauthAccount);
    }

    @DisplayName("알림을 조회할 때")
    @Nested
    class GetNotifications {

        @BeforeEach
        void setUp() {
            List<Notification> notifications = List.of(
                    NotificationFixtureBuilder.withMember(savedMember)
                            .createdAt(LocalDate.of(2025,8,15))
                            .build(),
                    NotificationFixtureBuilder.withMember(savedMember)
                            .createdAt(LocalDate.of(2025,8,15))
                            .build(),
                    NotificationFixtureBuilder.withMember(savedMember)
                            .createdAt(LocalDate.of(2025,8,15))
                            .build(),
                    NotificationFixtureBuilder.withMember(savedMember)
                            .createdAt(LocalDate.of(2025,8,15))
                            .build(),
                    NotificationFixtureBuilder.withMember(savedMember)
                            .createdAt(LocalDate.of(2025,8,15))
                            .build(),
                    NotificationFixtureBuilder.withMember(savedMember)
                            .createdAt(LocalDate.of(2025,8,15))
                            .build(),
                    NotificationFixtureBuilder.withMember(savedMember)
                            .createdAt(LocalDate.of(2025,8,15))
                            .build(),
                    NotificationFixtureBuilder.withMember(savedMember)
                            .createdAt(LocalDate.of(2025,8,15))
                            .build(),
                    NotificationFixtureBuilder.withMember(savedMember)
                            .createdAt(LocalDate.of(2025,8,15))
                            .build(),
                    NotificationFixtureBuilder.withMember(savedMember)
                            .createdAt(LocalDate.of(2025,8,15))
                            .build()
            );
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
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            ReadNotificationsResponse actual = objectMapper.readValue(json, ReadNotificationsResponse.class);

            assertSoftly(softly -> {
                softly.assertThat(actual.nextCursor()).isEqualTo(4);
                softly.assertThat(actual.readNotificationResponses().size()).isEqualTo(5);
                softly.assertThat(actual.readNotificationResponses())
                        .allMatch(r -> r.id() >= 5L);
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
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            ReadNotificationsResponse actual = objectMapper.readValue(json, ReadNotificationsResponse.class);

            assertSoftly(softly -> {
                softly.assertThat(actual.nextCursor()).isEqualTo(5);
                softly.assertThat(actual.readNotificationResponses().size()).isEqualTo(5);
                softly.assertThat(actual.readNotificationResponses())
                        .allMatch(r -> r.id() >= 4L);
            });
        }
    }

    @DisplayName("읽지 않은 알림 수를 조회할 때")
    @Nested
    class GetUnreadNotificationsCount {

        @BeforeEach
        void setUp() {
            List<Notification> notifications = List.of(
                    NotificationFixtureBuilder.withMember(savedMember)
                            .createdAt(LocalDate.of(2025,8,15))
                            .build(),
                    NotificationFixtureBuilder.withMember(savedMember)
                            .createdAt(LocalDate.of(2025,8,15))
                            .build(),
                    NotificationFixtureBuilder.withMember(savedMember)
                            .createdAt(LocalDate.of(2025,8,15))
                            .build(),
                    NotificationFixtureBuilder.withMember(savedMember)
                            .createdAt(LocalDate.of(2025,8,15))
                            .build(),
                    NotificationFixtureBuilder.withMember(savedMember)
                            .createdAt(LocalDate.of(2025,8,15))
                            .build(),
                    NotificationFixtureBuilder.withMember(savedMember)
                            .createdAt(LocalDate.of(2025,8,15))
                            .build(),
                    NotificationFixtureBuilder.withMember(savedMember)
                            .createdAt(LocalDate.of(2025,8,15))
                            .build(),
                    NotificationFixtureBuilder.withMember(savedMember)
                            .createdAt(LocalDate.of(2025,8,15))
                            .isRead(true)
                            .build(),
                    NotificationFixtureBuilder.withMember(savedMember)
                            .createdAt(LocalDate.of(2025,8,15))
                            .isRead(true)
                            .build(),
                    NotificationFixtureBuilder.withMember(savedMember)
                            .createdAt(LocalDate.of(2025,8,15))
                            .isRead(true)
                            .build()
            );
            notificationRepository.saveAll(notifications);
        }

        @DisplayName("유요한 요청이면 올바르게 반환한다")
        @Test
        void success_validMember() throws Exception {
            // when & then
            String json = mockMvc.perform(get("/notifications/unread-count")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            GetNotificationsCountResponse actual = objectMapper.readValue(json, GetNotificationsCountResponse.class);

            assertThat(actual.count()).isEqualTo(7);
        }
    }
}
