package backend.mulkkam.notification.dto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import backend.mulkkam.common.infrastructure.fcm.domain.Action;
import backend.mulkkam.common.infrastructure.fcm.dto.request.SendMessageByFcmTokensRequest;
import backend.mulkkam.common.infrastructure.fcm.dto.request.SendMessageByFcmTopicRequest;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.notification.domain.Notification;
import backend.mulkkam.notification.domain.NotificationType;
import backend.mulkkam.support.fixture.member.MemberFixtureBuilder;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class NotificationMessageTemplateTest {

    @DisplayName("알림 메시지 템플릿을 생성할 때")
    @Nested
    class CreateTemplate {

        @DisplayName("모든 필드를 올바르게 초기화한다")
        @Test
        void success_whenAllFieldsProvided() {
            // given
            String title = "물 마실 시간!";
            String body = "지금 물 한 잔 어떠세요?";
            Action action = Action.GO_HOME;
            NotificationType type = NotificationType.REMIND;

            // when
            NotificationMessageTemplate template = new NotificationMessageTemplate(title, body, action, type);

            // then
            assertSoftly(softly -> {
                softly.assertThat(template.title()).isEqualTo(title);
                softly.assertThat(template.body()).isEqualTo(body);
                softly.assertThat(template.action()).isEqualTo(action);
                softly.assertThat(template.type()).isEqualTo(type);
            });
        }
    }

    @DisplayName("단일 알림 엔티티로 변환할 때")
    @Nested
    class ToNotification {

        @DisplayName("멤버와 생성 시각으로 알림 엔티티를 생성한다")
        @Test
        void success_whenValidMemberAndTime() {
            // given
            NotificationMessageTemplate template = new NotificationMessageTemplate(
                    "제목",
                    "내용",
                    Action.GO_HOME,
                    NotificationType.REMIND
            );
            Member member = MemberFixtureBuilder.builder().buildWithId(1L);
            LocalDateTime createdAt = LocalDateTime.of(2025, 1, 15, 14, 30);

            // when
            Notification notification = template.toNotification(member, createdAt);

            // then
            assertSoftly(softly -> {
                softly.assertThat(notification.getNotificationType()).isEqualTo(NotificationType.REMIND);
                softly.assertThat(notification.getContent()).isEqualTo("내용");
                softly.assertThat(notification.getCreatedAt()).isEqualTo(createdAt);
                softly.assertThat(notification.getMember()).isEqualTo(member);
            });
        }
    }

    @DisplayName("여러 알림 엔티티로 변환할 때")
    @Nested
    class ToNotifications {

        @DisplayName("멤버 리스트로부터 알림 엔티티 리스트를 생성한다")
        @Test
        void success_whenMultipleMembers() {
            // given
            NotificationMessageTemplate template = new NotificationMessageTemplate(
                    "제목",
                    "내용",
                    Action.GO_HOME,
                    NotificationType.REMIND
            );
            Member member1 = MemberFixtureBuilder.builder().buildWithId(1L);
            Member member2 = MemberFixtureBuilder.builder().buildWithId(2L);
            Member member3 = MemberFixtureBuilder.builder().buildWithId(3L);
            List<Member> members = List.of(member1, member2, member3);
            LocalDateTime createdAt = LocalDateTime.of(2025, 1, 15, 14, 30);

            // when
            List<Notification> notifications = template.toNotifications(members, createdAt);

            // then
            assertSoftly(softly -> {
                softly.assertThat(notifications).hasSize(3);
                softly.assertThat(notifications).allMatch(n ->
                        n.getNotificationType() == NotificationType.REMIND &&
                        n.getContent().equals("내용") &&
                        n.getCreatedAt().equals(createdAt)
                );
            });
        }

        @DisplayName("빈 멤버 리스트는 빈 알림 리스트를 반환한다")
        @Test
        void success_whenEmptyMembers() {
            // given
            NotificationMessageTemplate template = new NotificationMessageTemplate(
                    "제목",
                    "내용",
                    Action.GO_HOME,
                    NotificationType.REMIND
            );
            List<Member> emptyMembers = List.of();
            LocalDateTime createdAt = LocalDateTime.of(2025, 1, 15, 14, 30);

            // when
            List<Notification> notifications = template.toNotifications(emptyMembers, createdAt);

            // then
            assertThat(notifications).isEmpty();
        }
    }

    @DisplayName("FCM 토픽 메시지 요청으로 변환할 때")
    @Nested
    class ToSendMessageByFcmTopicRequest {

        @DisplayName("토픽과 함께 FCM 메시지 요청을 생성한다")
        @Test
        void success_whenValidTopic() {
            // given
            NotificationMessageTemplate template = new NotificationMessageTemplate(
                    "물 마실 시간!",
                    "지금 물 한 잔 어떠세요?",
                    Action.GO_HOME,
                    NotificationType.REMIND
            );
            String topic = "all-users";

            // when
            SendMessageByFcmTopicRequest request = template.toSendMessageByFcmTopicRequest(topic);

            // then
            assertSoftly(softly -> {
                softly.assertThat(request.title()).isEqualTo("물 마실 시간!");
                softly.assertThat(request.body()).isEqualTo("지금 물 한 잔 어떠세요?");
                softly.assertThat(request.topic()).isEqualTo(topic);
                softly.assertThat(request.action()).isEqualTo(Action.GO_HOME);
            });
        }
    }

    @DisplayName("FCM 토큰 메시지 요청으로 변환할 때")
    @Nested
    class ToSendMessageByFcmTokensRequest {

        @DisplayName("토큰 리스트와 함께 FCM 메시지 요청을 생성한다")
        @Test
        void success_whenValidTokens() {
            // given
            NotificationMessageTemplate template = new NotificationMessageTemplate(
                    "물 마실 시간!",
                    "지금 물 한 잔 어떠세요?",
                    Action.GO_HOME,
                    NotificationType.REMIND
            );
            List<String> tokens = List.of("token1", "token2", "token3");

            // when
            SendMessageByFcmTokensRequest request = template.toSendMessageByFcmTokensRequest(tokens);

            // then
            assertSoftly(softly -> {
                softly.assertThat(request.title()).isEqualTo("물 마실 시간!");
                softly.assertThat(request.body()).isEqualTo("지금 물 한 잔 어떠세요?");
                softly.assertThat(request.tokens()).containsExactly("token1", "token2", "token3");
                softly.assertThat(request.action()).isEqualTo(Action.GO_HOME);
            });
        }

        @DisplayName("빈 토큰 리스트로도 요청을 생성할 수 있다")
        @Test
        void success_whenEmptyTokens() {
            // given
            NotificationMessageTemplate template = new NotificationMessageTemplate(
                    "제목",
                    "내용",
                    Action.GO_HOME,
                    NotificationType.REMIND
            );
            List<String> emptyTokens = List.of();

            // when
            SendMessageByFcmTokensRequest request = template.toSendMessageByFcmTokensRequest(emptyTokens);

            // then
            assertThat(request.tokens()).isEmpty();
        }
    }
}