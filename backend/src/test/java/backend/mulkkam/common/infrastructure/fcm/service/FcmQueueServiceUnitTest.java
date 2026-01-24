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
import backend.mulkkam.common.infrastructure.fcm.domain.NotificationOutboxTargetType;
import backend.mulkkam.common.infrastructure.fcm.dto.request.SendMessageByFcmTokensRequest;
import backend.mulkkam.common.infrastructure.fcm.repository.NotificationOutboxRepository;
import backend.mulkkam.device.repository.DeviceRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FcmQueueServiceUnitTest {

    @Mock
    private NotificationOutboxRepository notificationOutboxRepository;

    @Mock
    private DeviceRepository deviceRepository;

    @Mock
    private FcmClient fcmClient;

    @DisplayName("토큰 리스트 요청은 토큰당 1건의 아웃박스로 저장된다")
    @Test
    void enqueueTokens_savesOutboxPerToken() {
        // given
        FcmQueueService service = new FcmQueueService(notificationOutboxRepository, deviceRepository, fcmClient, 100, 4, 1);
        SendMessageByFcmTokensRequest request = new SendMessageByFcmTokensRequest(
                "title",
                "body",
                List.of("token-1", "token-2"),
                Action.GO_HOME
        );

        // when
        service.enqueueTokens(request);

        // then
        ArgumentCaptor<List<NotificationOutbox>> captor = ArgumentCaptor.forClass(List.class);
        verify(notificationOutboxRepository).saveAll(captor.capture());

        List<NotificationOutbox> outboxes = captor.getValue();
        assertThat(outboxes).hasSize(2);
        assertThat(outboxes)
                .allMatch(outbox -> outbox.getTargetType() == NotificationOutboxTargetType.TOKEN)
                .allMatch(outbox -> outbox.getStatus() == NotificationOutboxStatus.PENDING);
        assertThat(outboxes)
                .extracting(NotificationOutbox::getToken)
                .containsExactlyInAnyOrder("token-1", "token-2");
    }

    @DisplayName("전송 성공 시 아웃박스 상태가 성공으로 갱신된다")
    @Test
    void processPendingQueue_marksSuccess() {
        // given
        FcmQueueService service = new FcmQueueService(notificationOutboxRepository, deviceRepository, fcmClient, 10, 4, 1);
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
        service.processPendingQueue();

        // then
        assertThat(outbox.getStatus()).isEqualTo(NotificationOutboxStatus.SUCCESS);
        assertThat(outbox.getAttemptCount()).isEqualTo(1);
        assertThat(outbox.getSentAt()).isNotNull();
    }

    @DisplayName("전송 실패 시 재시도 대기 상태로 갱신된다")
    @Test
    void processPendingQueue_marksPendingWithBackoff() {
        // given
        FcmQueueService service = new FcmQueueService(notificationOutboxRepository, deviceRepository, fcmClient, 10, 4, 1);
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
        service.processPendingQueue();

        // then
        assertThat(outbox.getStatus()).isEqualTo(NotificationOutboxStatus.PENDING);
        assertThat(outbox.getAttemptCount()).isEqualTo(1);
        assertThat(outbox.getNextAttemptAt()).isAfter(before);
        assertThat(outbox.getLastErrorCode()).isEqualTo("RuntimeException");
        assertThat(outbox.getLastErrorMessage()).isEqualTo("boom");
    }

    @DisplayName("최대 시도 횟수에 도달하면 실패 상태로 갱신된다")
    @Test
    void processPendingQueue_marksFailedWhenMaxAttemptsReached() {
        // given
        FcmQueueService service = new FcmQueueService(notificationOutboxRepository, deviceRepository, fcmClient, 10, 1, 1);
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
        service.processPendingQueue();

        // then
        assertThat(outbox.getStatus()).isEqualTo(NotificationOutboxStatus.FAILED);
        assertThat(outbox.getAttemptCount()).isEqualTo(1);
    }

    @DisplayName("UNREGISTERED 에러는 토큰 삭제 후 실패 처리된다")
    @Test
    void processPendingQueue_removesTokenOnUnregistered() {
        // given
        FcmQueueService service = new FcmQueueService(notificationOutboxRepository, deviceRepository, fcmClient, 10, 4, 1);
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
        service.processPendingQueue();

        // then
        verify(deviceRepository).deleteByToken("token-1");
        assertThat(outbox.getStatus()).isEqualTo(NotificationOutboxStatus.FAILED);
    }

    @DisplayName("INTERNAL 에러는 재시도 대상으로 처리된다")
    @Test
    void processPendingQueue_retriesOnInternalError() {
        // given
        FcmQueueService service = new FcmQueueService(notificationOutboxRepository, deviceRepository, fcmClient, 10, 4, 1);
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
        service.processPendingQueue();

        // then
        assertThat(outbox.getStatus()).isEqualTo(NotificationOutboxStatus.PENDING);
        assertThat(outbox.getAttemptCount()).isEqualTo(1);
    }
}
