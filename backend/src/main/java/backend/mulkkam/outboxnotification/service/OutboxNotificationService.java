package backend.mulkkam.outboxnotification.service;

import backend.mulkkam.device.repository.DeviceRepository;
import backend.mulkkam.notification.dto.NotificationMessageTemplate;
import backend.mulkkam.outboxnotification.domain.OutboxNotification;
import backend.mulkkam.outboxnotification.domain.OutboxNotification.Status;
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

    private final DeviceRepository deviceRepository;
    private final OutboxNotificationJdbcRepository outboxNotificationJdbcRepository;

    @Transactional
    public void enqueueOutbox(
            List<Long> memberIds,
            LocalDateTime time,
            NotificationMessageTemplate template
    ) {
        List<Object[]> memberTokenPairs = deviceRepository.findMemberIdAndTokenByMemberIdIn(memberIds);
        List<OutboxNotification> outboxList = new ArrayList<>();

        for (Object[] pair : memberTokenPairs) {
            Long memberId = (Long) pair[0];
            String token = (String) pair[1];
            String dedupeKey = buildDedupeKey("REMIND", memberId, time);

            OutboxNotification outboxNotification = new OutboxNotification(
                    "REMIND",
                    memberId,
                    token,
                    template.title(),
                    template.body(),
                    Status.READY,
                    dedupeKey,
                    0,
                    null,
                    null
            );
            outboxList.add(outboxNotification);
        }
        outboxNotificationJdbcRepository.batchSaveIgnoringDuplicate(outboxList);
    }

    public String buildDedupeKey(
            String type,
            Long memberId,
            LocalDateTime time
    ) {
        return type + ":" + memberId + ":" + time;
    }
}
