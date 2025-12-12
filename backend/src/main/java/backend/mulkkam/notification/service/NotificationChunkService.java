package backend.mulkkam.notification.service;

import backend.mulkkam.notification.dto.NotificationInsertDto;
import backend.mulkkam.notification.dto.NotificationMessageTemplate;
import backend.mulkkam.notification.repository.NotificationBatchRepository;
import backend.mulkkam.outboxnotification.service.OutboxNotificationService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class NotificationChunkService {

    private static final int CHUNK_SIZE = 1_000;

    private final OutboxNotificationService outboxNotificationService;
    private final NotificationBatchRepository notificationBatchRepository;

    @Transactional
    public void processChunk(
            List<Long> memberIds,
            LocalDateTime now,
            NotificationMessageTemplate template
    ) {
        saveNotificationsAndEvents(memberIds, now, template);
    }

    private void saveNotificationsAndEvents(
            List<Long> memberIds,
            LocalDateTime localDateTime,
            NotificationMessageTemplate template
    ) {
        saveNotifications(memberIds, template);
        outboxNotificationService.enqueueOutbox(memberIds, localDateTime, template);
    }

    private void saveNotifications(
            List<Long> memberIds,
            NotificationMessageTemplate template
    ) {
        List<NotificationInsertDto> notificationInsertDtos = memberIds.stream()
                .map(memberId -> new NotificationInsertDto(template, memberId))
                .toList();
        notificationBatchRepository.batchInsert(notificationInsertDtos, CHUNK_SIZE);
    }
}
