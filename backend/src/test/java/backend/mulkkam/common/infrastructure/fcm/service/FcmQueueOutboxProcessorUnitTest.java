package backend.mulkkam.common.infrastructure.fcm.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.common.exception.errorCode.FirebaseErrorCode;
import backend.mulkkam.common.infrastructure.fcm.domain.Action;
import backend.mulkkam.common.infrastructure.fcm.domain.NotificationOutbox;
import backend.mulkkam.common.infrastructure.fcm.domain.NotificationOutboxStatus;
import backend.mulkkam.common.infrastructure.fcm.repository.NotificationOutboxRepository;
import backend.mulkkam.device.repository.DeviceRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FcmQueueOutboxProcessorUnitTest {

    @Mock
    private NotificationOutboxRepository notificationOutboxRepository;

    @Mock
    private DeviceRepository deviceRepository;

    @Mock
    private FcmClient fcmClient;

    @DisplayName("전송 성공 시 아웃박스 상태가 성공으로 갱신된다")
    @Test
    void processNext_marksSuccess() {
        // given
        FcmQueueOutboxProcessor processor = new FcmQueueOutboxProcessor(
                notificationOutboxRepository,
                deviceRepository,
                fcmClient,
                1
        );
        NotificationOutbox outbox = NotificationOutbox.forToken(
                "title",
                "body",
                "token-1",
                Action.GO_HOME,
                4,
                LocalDateTime.now()
        );
        when(notificationOutboxRepository.findPendingForUpdate(anyString(), any(LocalDateTime.class), anyInt()))
                .thenReturn(List.of(outbox));

        // when
        boolean processed = processor.processNext(LocalDateTime.now());

        // then
        assertThat(processed).isTrue();
        assertThat(outbox.getStatus()).isEqualTo(NotificationOutboxStatus.SUCCESS);
        assertThat(outbox.getAttemptCount()).isEqualTo(1);
        assertThat(outbox.getSentAt()).isNotNull();
    }

    @DisplayName("전송 실패 시 재시도 대기 상태로 갱신된다")
    @Test
    void processNext_marksPendingWithBackoff() {
        // given
        FcmQueueOutboxProcessor processor = new FcmQueueOutboxProcessor(
                notificationOutboxRepository,
                deviceRepository,
                fcmClient,
                1
        );
        NotificationOutbox outbox = NotificationOutbox.forToken(
                "title",
                "body",
                "token-1",
                Action.GO_HOME,
                4,
                LocalDateTime.now()
        );
        when(notificationOutboxRepository.findPendingForUpdate(anyString(), any(LocalDateTime.class), anyInt()))
                .thenReturn(List.of(outbox));
        doThrow(new RuntimeException("boom"))
                .when(fcmClient)
                .sendMessageByToken(any());

        LocalDateTime before = LocalDateTime.now();

        // when
        processor.processNext(LocalDateTime.now());

        // then
        assertThat(outbox.getStatus()).isEqualTo(NotificationOutboxStatus.PENDING);
        assertThat(outbox.getAttemptCount()).isEqualTo(1);
        assertThat(outbox.getNextAttemptAt()).isAfter(before);
        assertThat(outbox.getLastErrorCode()).isEqualTo("RuntimeException");
        assertThat(outbox.getLastErrorMessage()).isEqualTo("boom");
    }

    @DisplayName("최대 시도 횟수에 도달하면 실패 상태로 갱신된다")
    @Test
    void processNext_marksFailedWhenMaxAttemptsReached() {
        // given
        FcmQueueOutboxProcessor processor = new FcmQueueOutboxProcessor(
                notificationOutboxRepository,
                deviceRepository,
                fcmClient,
                1
        );
        NotificationOutbox outbox = NotificationOutbox.forToken(
                "title",
                "body",
                "token-1",
                Action.GO_HOME,
                1,
                LocalDateTime.now()
        );
        when(notificationOutboxRepository.findPendingForUpdate(anyString(), any(LocalDateTime.class), anyInt()))
                .thenReturn(List.of(outbox));
        doThrow(new RuntimeException("boom"))
                .when(fcmClient)
                .sendMessageByToken(any());

        // when
        processor.processNext(LocalDateTime.now());

        // then
        assertThat(outbox.getStatus()).isEqualTo(NotificationOutboxStatus.FAILED);
        assertThat(outbox.getAttemptCount()).isEqualTo(1);
    }

    @DisplayName("UNREGISTERED 에러는 토큰 삭제 후 실패 처리된다")
    @Test
    void processNext_removesTokenOnUnregistered() {
        // given
        FcmQueueOutboxProcessor processor = new FcmQueueOutboxProcessor(
                notificationOutboxRepository,
                deviceRepository,
                fcmClient,
                1
        );
        NotificationOutbox outbox = NotificationOutbox.forToken(
                "title",
                "body",
                "token-1",
                Action.GO_HOME,
                4,
                LocalDateTime.now()
        );
        when(notificationOutboxRepository.findPendingForUpdate(anyString(), any(LocalDateTime.class), anyInt()))
                .thenReturn(List.of(outbox));
        doThrow(new CommonException(FirebaseErrorCode.UNREGISTERED))
                .when(fcmClient)
                .sendMessageByToken(any());

        // when
        processor.processNext(LocalDateTime.now());

        // then
        verify(deviceRepository).deleteByToken("token-1");
        assertThat(outbox.getStatus()).isEqualTo(NotificationOutboxStatus.FAILED);
    }

    @DisplayName("INTERNAL 에러는 재시도 대상으로 처리된다")
    @Test
    void processNext_retriesOnInternalError() {
        // given
        FcmQueueOutboxProcessor processor = new FcmQueueOutboxProcessor(
                notificationOutboxRepository,
                deviceRepository,
                fcmClient,
                1
        );
        NotificationOutbox outbox = NotificationOutbox.forToken(
                "title",
                "body",
                "token-1",
                Action.GO_HOME,
                4,
                LocalDateTime.now()
        );
        when(notificationOutboxRepository.findPendingForUpdate(anyString(), any(LocalDateTime.class), anyInt()))
                .thenReturn(List.of(outbox));
        doThrow(new CommonException(FirebaseErrorCode.INTERNAL))
                .when(fcmClient)
                .sendMessageByToken(any());

        // when
        processor.processNext(LocalDateTime.now());

        // then
        assertThat(outbox.getStatus()).isEqualTo(NotificationOutboxStatus.PENDING);
        assertThat(outbox.getAttemptCount()).isEqualTo(1);
    }
}
