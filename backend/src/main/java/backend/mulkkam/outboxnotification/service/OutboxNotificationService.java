package backend.mulkkam.outboxnotification.service;

import backend.mulkkam.device.repository.DeviceRepository;
import backend.mulkkam.notification.dto.NotificationMessageTemplate;
import backend.mulkkam.outboxnotification.domain.OutboxNotification;
import backend.mulkkam.outboxnotification.repository.OutboxNotificationJdbcRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OutboxNotificationService {
    
    private static final int FCM_BATCH_SIZE = 500;

    private final DeviceRepository deviceRepository;
    private final OutboxNotificationJdbcRepository outboxNotificationJdbcRepository;

    @Transactional
    public void enqueueOutbox(List<Long> memberIds, LocalDateTime now, NotificationMessageTemplate template) {

        List<Object[]> memberTokenPairs = deviceRepository.findMemberIdAndTokenByMemberIdIn(memberIds);
        List<OutboxNotification> outboxList = new ArrayList<>();

        for (Object[] pair : memberTokenPairs) {
            Long memberId = (Long) pair[0];
            String token = (String) pair[1];
            String dedupeKey = buildDedupeKey("REMIND", memberId, now, token);

            OutboxNotification outbox = OutboxNotification.builder()
                    .type("REMIND")
                    .memberId(memberId)
                    .token(token)
                    .title(template.title())
                    .body(template.body())
                    .status(OutboxNotification.Status.READY)
                    .dedupeKey(dedupeKey)
                    .build();

            outboxList.add(outbox);
        }
        outboxNotificationJdbcRepository.batchSaveIgnoringDuplicate(outboxList);
    }

    public String buildDedupeKey(String type, Long memberId, LocalDateTime dateTime, String token) {
        return type + ":" + memberId + ":" + dateTime + ":" + token;
    }
}
