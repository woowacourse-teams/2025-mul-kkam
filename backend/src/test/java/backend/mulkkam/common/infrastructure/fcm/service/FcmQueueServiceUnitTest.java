package backend.mulkkam.common.infrastructure.fcm.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import backend.mulkkam.common.infrastructure.fcm.domain.Action;
import backend.mulkkam.common.infrastructure.fcm.domain.NotificationOutbox;
import backend.mulkkam.common.infrastructure.fcm.domain.NotificationOutboxStatus;
import backend.mulkkam.common.infrastructure.fcm.domain.NotificationOutboxTargetType;
import backend.mulkkam.common.infrastructure.fcm.dto.request.SendMessageByFcmTokensRequest;
import backend.mulkkam.common.infrastructure.fcm.repository.NotificationOutboxRepository;
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
    private FcmQueueOutboxProcessor fcmQueueOutboxProcessor;

    @DisplayName("토큰 리스트 요청은 토큰당 1건의 아웃박스로 저장된다")
    @Test
    void enqueueTokens_savesOutboxPerToken() {
        // given
        FcmQueueService service = new FcmQueueService(notificationOutboxRepository, fcmQueueOutboxProcessor, 100, 4);
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

    @DisplayName("배치 크기만큼 처리하도록 아웃박스 프로세서를 호출한다")
    @Test
    void processPendingQueue_invokesProcessorUpToBatchSize() {
        // given
        FcmQueueService service = new FcmQueueService(notificationOutboxRepository, fcmQueueOutboxProcessor, 2, 4);
        when(fcmQueueOutboxProcessor.processNext(any(LocalDateTime.class)))
                .thenReturn(true)
                .thenReturn(false);

        // when
        service.processPendingQueue();

        // then
        verify(fcmQueueOutboxProcessor, times(2)).processNext(any(LocalDateTime.class));
    }
}
