package backend.mulkkam.outboxnotification.service;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import backend.mulkkam.common.infrastructure.fcm.service.FcmClient;
import backend.mulkkam.outboxnotification.domain.OutboxNotification;
import backend.mulkkam.outboxnotification.domain.OutboxNotification.Status;
import backend.mulkkam.outboxnotification.repository.OutboxNotificationRepository;
import backend.mulkkam.support.fixture.OutboxNotificationFixtureBuilder;
import backend.mulkkam.support.service.ServiceIntegrationTest;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MessagingErrorCode;
import com.google.firebase.messaging.SendResponse;
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
                        .type("REMIND")
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
                        .type("REMIND")
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
                        .type("REMIND")
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
            BatchResponse batchResponse = createSuccessfulBatchResponse(3);
            when(fcmClient.sendMulticast(anyString(), anyString(), anyString(), anyList()))
                    .thenReturn(batchResponse);

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

            verify(fcmClient, times(1)).sendMulticast(
                    eq("물 마실 시간이에요"),
                    eq("오늘도 건강한 하루 되세요!"),
                    anyString(),
                    anyList()
            );
        }

        @DisplayName("일부 전송 실패 시 영구 실패는 FAIL, 일시 실패는 RETRY로 변경된다")
        @Test
        void success_mixedResultsHandledCorrectly() {
            // given
            BatchResponse batchResponse = createMixedBatchResponse();
            when(fcmClient.sendMulticast(anyString(), anyString(), anyString(), anyList()))
                    .thenReturn(batchResponse);

            // when
            outboxDispatcher.dispatch();

            // then
            OutboxNotification updated1 = outboxNotificationRepository.findById(outbox1.getId()).orElseThrow();
            OutboxNotification updated2 = outboxNotificationRepository.findById(outbox2.getId()).orElseThrow();
            OutboxNotification updated3 = outboxNotificationRepository.findById(outbox3.getId()).orElseThrow();

            assertSoftly(softly -> {
                // 성공
                softly.assertThat(updated1.getStatus()).isEqualTo(Status.SENT);
                softly.assertThat(updated1.getAttemptCount()).isEqualTo(1);

                // 일시적 실패 -> RETRY
                softly.assertThat(updated2.getStatus()).isEqualTo(Status.RETRY);
                softly.assertThat(updated2.getAttemptCount()).isEqualTo(1);
                softly.assertThat(updated2.getNextAttemptAt()).isNotNull();
                softly.assertThat(updated2.getLastError()).contains("UNAVAILABLE");

                // 영구 실패 -> FAIL
                softly.assertThat(updated3.getStatus()).isEqualTo(Status.FAIL);
                softly.assertThat(updated3.getAttemptCount()).isEqualTo(1);
                softly.assertThat(updated3.getLastError()).contains("INVALID_ARGUMENT");
            });
        }

        @DisplayName("시도 횟수가 3회 이상이면 FAIL로 변경된다")
        @Test
        void success_maxRetriesExceededMarkedAsFail() {
            // given
            outboxNotificationRepository.deleteAll();
            OutboxNotification retryOutbox = outboxNotificationRepository.save(
                    OutboxNotificationFixtureBuilder.builder()
                            .type("REMIND")
                            .memberId(1L)
                            .token("token-retry")
                            .title("물 마실 시간이에요")
                            .body("오늘도 건강한 하루 되세요!")
                            .status(Status.RETRY)
                            .attemptCount(2)
                            .nextAttemptAt(LocalDateTime.now().minusMinutes(1))
                            .dedupeKey("REMIND:1:retry:token-retry")
                            .build()
            );

            BatchResponse batchResponse = createFailedBatchResponse(1, MessagingErrorCode.UNAVAILABLE);
            when(fcmClient.sendMulticast(anyString(), anyString(), anyString(), anyList()))
                    .thenReturn(batchResponse);

            // when
            outboxDispatcher.dispatch();

            // then
            OutboxNotification updated = outboxNotificationRepository.findById(retryOutbox.getId()).orElseThrow();

            assertSoftly(softly -> {
                softly.assertThat(updated.getStatus()).isEqualTo(Status.FAIL);
                softly.assertThat(updated.getAttemptCount()).isEqualTo(3);
                softly.assertThat(updated.getLastError()).isNotNull();
            });
        }

        @DisplayName("RETRY 상태이면서 nextAttemptAt이 아직 도래하지 않은 경우 dispatch하지 않는다")
        @Test
        void success_doesNotDispatchWhenNextAttemptNotReached() {
            outboxNotificationRepository.deleteAll();

            // given
            outboxNotificationRepository.save(
                    OutboxNotificationFixtureBuilder.builder()
                            .type("REMIND")
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

            // when
            outboxDispatcher.dispatch();

            // then
            verify(fcmClient, never()).sendMulticast(anyString(), anyString(), anyString(), anyList());
        }

        @DisplayName("SENDING 상태의 outbox는 재처리되지 않는다")
        @Test
        void success_doesNotReprocessSendingStatus() {
            outboxNotificationRepository.deleteAll();
            
            // given
            outboxNotificationRepository.save(
                    OutboxNotificationFixtureBuilder.builder()
                            .type("REMIND")
                            .memberId(1L)
                            .token("token-sending")
                            .title("물 마실 시간이에요")
                            .body("오늘도 건강한 하루 되세요!")
                            .status(Status.SENDING)
                            .attemptCount(1)
                            .dedupeKey("REMIND:1:sending:token-sending")
                            .build()
            );

            // when
            outboxDispatcher.dispatch();

            // then
            verify(fcmClient, never()).sendMulticast(anyString(), anyString(), anyString(), anyList());
        }

        @DisplayName("빈 outbox 큐에서 dispatch 시 아무 동작도 하지 않는다")
        @Test
        void success_noActionWhenQueueIsEmpty() {
            // given
            outboxNotificationRepository.deleteAll();

            // when
            outboxDispatcher.dispatch();

            // then
            verify(fcmClient, never()).sendMulticast(anyString(), anyString(), anyString(), anyList());
        }

        @DisplayName("첫 번째 outbox의 title과 body를 사용하여 multicast를 전송한다")
        @Test
        void success_usesTitleAndBodyFromFirstOutbox() {
            // given
            BatchResponse batchResponse = createSuccessfulBatchResponse(3);
            when(fcmClient.sendMulticast(anyString(), anyString(), anyString(), anyList()))
                    .thenReturn(batchResponse);

            // when
            outboxDispatcher.dispatch();

            // then
            verify(fcmClient).sendMulticast(
                    eq("물 마실 시간이에요"),
                    eq("오늘도 건강한 하루 되세요!"),
                    anyString(),
                    anyList()
            );
        }
    }

    private BatchResponse createSuccessfulBatchResponse(int count) {
        List<SendResponse> responses = java.util.stream.IntStream.range(0, count)
                .mapToObj(i -> Mockito.mock(SendResponse.class))
                .peek(response -> when(response.isSuccessful()).thenReturn(true))
                .toList();

        BatchResponse batchResponse = Mockito.mock(BatchResponse.class);
        when(batchResponse.getResponses()).thenReturn(responses);
        when(batchResponse.getSuccessCount()).thenReturn(count);
        when(batchResponse.getFailureCount()).thenReturn(0);

        return batchResponse;
    }

    private BatchResponse createMixedBatchResponse() {
        // token-1: 성공
        SendResponse success = Mockito.mock(SendResponse.class);
        when(success.isSuccessful()).thenReturn(true);

        // token-2: 일시적 실패 (UNAVAILABLE)
        SendResponse transientFailure = Mockito.mock(SendResponse.class);
        when(transientFailure.isSuccessful()).thenReturn(false);
        FirebaseMessagingException transientException = Mockito.mock(FirebaseMessagingException.class);
        when(transientException.getMessagingErrorCode()).thenReturn(MessagingErrorCode.UNAVAILABLE);
        when(transientException.getMessage()).thenReturn("Service unavailable");
        when(transientFailure.getException()).thenReturn(transientException);

        // token-3: 영구 실패 (INVALID_ARGUMENT)
        SendResponse permanentFailure = Mockito.mock(SendResponse.class);
        when(permanentFailure.isSuccessful()).thenReturn(false);
        FirebaseMessagingException permanentException = Mockito.mock(FirebaseMessagingException.class);
        when(permanentException.getMessagingErrorCode()).thenReturn(MessagingErrorCode.INVALID_ARGUMENT);
        when(permanentException.getMessage()).thenReturn("Invalid token");
        when(permanentFailure.getException()).thenReturn(permanentException);

        List<SendResponse> responses = List.of(success, transientFailure, permanentFailure);

        BatchResponse batchResponse = Mockito.mock(BatchResponse.class);
        when(batchResponse.getResponses()).thenReturn(responses);
        when(batchResponse.getSuccessCount()).thenReturn(1);
        when(batchResponse.getFailureCount()).thenReturn(2);

        return batchResponse;
    }

    private BatchResponse createFailedBatchResponse(
            int count,
            MessagingErrorCode errorCode
    ) {
        List<SendResponse> responses = java.util.stream.IntStream.range(0, count)
                .mapToObj(i -> {
                    SendResponse response = Mockito.mock(SendResponse.class);
                    when(response.isSuccessful()).thenReturn(false);

                    FirebaseMessagingException exception = Mockito.mock(FirebaseMessagingException.class);
                    when(exception.getMessagingErrorCode()).thenReturn(errorCode);
                    when(exception.getMessage()).thenReturn("Error: " + errorCode);
                    when(response.getException()).thenReturn(exception);

                    return response;
                })
                .toList();

        BatchResponse batchResponse = Mockito.mock(BatchResponse.class);
        when(batchResponse.getResponses()).thenReturn(responses);
        when(batchResponse.getSuccessCount()).thenReturn(0);
        when(batchResponse.getFailureCount()).thenReturn(count);

        return batchResponse;
    }
}
