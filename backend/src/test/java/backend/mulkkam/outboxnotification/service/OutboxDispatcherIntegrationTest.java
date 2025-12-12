package backend.mulkkam.outboxnotification.service;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import backend.mulkkam.common.infrastructure.fcm.dto.request.SendMessageByFcmTokenRequest;
import backend.mulkkam.common.infrastructure.fcm.service.FcmClient;
import backend.mulkkam.notification.domain.NotificationType;
import backend.mulkkam.outboxnotification.domain.OutboxNotification;
import backend.mulkkam.outboxnotification.domain.OutboxNotification.Status;
import backend.mulkkam.outboxnotification.repository.OutboxNotificationRepository;
import backend.mulkkam.support.fixture.OutboxNotificationFixtureBuilder;
import backend.mulkkam.support.service.ServiceIntegrationTest;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MessagingErrorCode;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

class OutboxDispatcherIntegrationTest extends ServiceIntegrationTest {

    @Autowired
    private OutboxDispatcher outboxDispatcher;

    @Autowired
    private OutboxNotificationRepository outboxNotificationRepository;

    @MockitoBean
    private FcmClient fcmClient;

    private OutboxNotification outbox1;
    private OutboxNotification outbox2;
    private OutboxNotification outbox3;

    @BeforeEach
    void setUp() {
        outboxNotificationRepository.deleteAll();

        outbox1 = outboxNotificationRepository.save(
                OutboxNotificationFixtureBuilder.builder()
                        .type(NotificationType.REMIND)
                        .memberId(1L)
                        .token("token-1")
                        .title("물 마실 시간이에요")
                        .body("오늘도 건강한 하루 되세요!")
                        .status(Status.READY)
                        .dedupeKey("REMIND:1:" + LocalDateTime.now() + ":token-1")
                        .build()
        );

        outbox2 = outboxNotificationRepository.save(
                OutboxNotificationFixtureBuilder.builder()
                        .type(NotificationType.REMIND)
                        .memberId(2L)
                        .token("token-2")
                        .title("물 마실 시간이에요")
                        .body("오늘도 건강한 하루 되세요!")
                        .status(Status.READY)
                        .dedupeKey("REMIND:2:" + LocalDateTime.now() + ":token-2")
                        .build()
        );

        outbox3 = outboxNotificationRepository.save(
                OutboxNotificationFixtureBuilder.builder()
                        .type(NotificationType.REMIND)
                        .memberId(3L)
                        .token("token-3")
                        .title("물 마실 시간이에요")
                        .body("오늘도 건강한 하루 되세요!")
                        .status(Status.READY)
                        .dedupeKey("REMIND:3:" + LocalDateTime.now() + ":token-3")
                        .build()
        );
    }

    @DisplayName("Outbox를 dispatch할 때")
    @Nested
    class Dispatch {

        @DisplayName("FCM 전송 성공 시 모든 outbox가 SENT 상태로 변경된다")
        @Test
        void success_allOutboxesMarkedAsSentWhenFcmSucceeds() {
            // given
            Mockito.doNothing()
                    .when(fcmClient)
                    .sendMessageByToken(any(SendMessageByFcmTokenRequest.class));

            // when
            outboxDispatcher.dispatch();

            // then
            List<OutboxNotification> updatedOutboxes = outboxNotificationRepository.findAll();

            assertSoftly(softly -> {
                softly.assertThat(updatedOutboxes).hasSize(3);
                softly.assertThat(updatedOutboxes)
                        .allMatch(outbox -> outbox.getStatus() == Status.SENT);
                softly.assertThat(updatedOutboxes)
                        .allMatch(outbox -> outbox.getAttemptCount() == 1);
            });

            verify(fcmClient, times(3))
                    .sendMessageByToken(any(SendMessageByFcmTokenRequest.class));
        }

        @DisplayName("일부 전송 실패 시 영구 실패는 FAIL, 일시 실패는 RETRY로 변경된다")
        @Test
        void success_mixedResultsHandledCorrectly() {
            // given
            mockTokenFailure("token-2", MessagingErrorCode.UNAVAILABLE);
            mockTokenFailure("token-3", MessagingErrorCode.INVALID_ARGUMENT);

            // when
            outboxDispatcher.dispatch();

            // then
            OutboxNotification updated1 = outboxNotificationRepository.findById(outbox1.getId()).orElseThrow();
            OutboxNotification updated2 = outboxNotificationRepository.findById(outbox2.getId()).orElseThrow();
            OutboxNotification updated3 = outboxNotificationRepository.findById(outbox3.getId()).orElseThrow();

            assertSoftly(softly -> {
                softly.assertThat(updated1.getStatus()).isEqualTo(Status.SENT);

                softly.assertThat(updated2.getStatus()).isEqualTo(Status.RETRY);
                softly.assertThat(updated2.getLastError()).contains("UNAVAILABLE");

                softly.assertThat(updated3.getStatus()).isEqualTo(Status.FAIL);
                softly.assertThat(updated3.getLastError()).contains("INVALID_ARGUMENT");
            });
        }

        @DisplayName("RETRY 상태이면서 nextAttemptAt이 아직 도래하지 않은 경우 dispatch하지 않는다")
        @Test
        void success_doesNotDispatchWhenNextAttemptNotReached() {
            outboxNotificationRepository.deleteAll();

            outboxNotificationRepository.save(
                    OutboxNotificationFixtureBuilder.builder()
                            .type(NotificationType.REMIND)
                            .memberId(1L)
                            .token("token-future")
                            .title("물 마실 시간이에요")
                            .body("오늘도 건강한 하루 되세요!")
                            .status(Status.RETRY)
                            .attemptCount(1)
                            .nextAttemptAt(LocalDateTime.now().plusHours(1))
                            .dedupeKey("REMIND:1:future:token-future")
                            .build()
            );

            outboxDispatcher.dispatch();

            verify(fcmClient, never()).sendMessageByToken(any());
        }
    }

    private void mockTokenFailure(String token, MessagingErrorCode errorCode) {
        FirebaseMessagingException exception = Mockito.mock(FirebaseMessagingException.class);
        Mockito.when(exception.getMessagingErrorCode()).thenReturn(errorCode);

        Mockito.doThrow(new RuntimeException(exception))
                .when(fcmClient)
                .sendMessageByToken(argThat(req -> req.token().equals(token)));
    }
}
