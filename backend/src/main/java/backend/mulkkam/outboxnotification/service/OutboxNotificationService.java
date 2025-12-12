package backend.mulkkam.outboxnotification.service;

import backend.mulkkam.device.repository.DeviceRepository;
import backend.mulkkam.notification.dto.NotificationMessageTemplate;
import backend.mulkkam.outboxnotification.domain.OutboxNotification;
import backend.mulkkam.outboxnotification.domain.OutboxNotification.Status;
import backend.mulkkam.outboxnotification.repository.OutboxNotificationJdbcRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OutboxNotificationService {

    private final DeviceRepository deviceRepository;
    private final OutboxNotificationJdbcRepository outboxNotificationJdbcRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void enqueueOutbox(
            List<Long> memberIds,
            LocalDateTime time,
            NotificationMessageTemplate template,
            String messageType
    ) {
        List<Object[]> memberTokenPairs = deviceRepository.findMemberIdAndTokenByMemberIdIn(memberIds);
        List<OutboxNotification> outboxList = new ArrayList<>();

        for (Object[] pair : memberTokenPairs) {
            Long memberId = (Long) pair[0];
            String token = (String) pair[1];
            String dedupeKey = buildIdempotencyKey(messageType, memberId, time, token);

            OutboxNotification outboxNotification = new OutboxNotification(
                    messageType,
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

    public String buildIdempotencyKey(
            String type,
            Long memberId,
            LocalDateTime time,
            String token
    ) {
        return type + ":" + memberId + ":" + time + ":" + token;
    }
}
