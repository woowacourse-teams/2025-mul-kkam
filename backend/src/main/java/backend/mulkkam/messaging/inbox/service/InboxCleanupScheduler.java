package backend.mulkkam.messaging.inbox.service;

import backend.mulkkam.messaging.inbox.repository.InboxMessageRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Component
@Profile("consumer")
public class InboxCleanupScheduler {

    private final InboxMessageRepository inboxMessageRepository;

    @Value("${consumer.inbox.retention-days:7}")
    private int retentionDays;

    @Scheduled(cron = "0 0 3 * * *") // 매일 새벽 3시
    @Transactional
    public void cleanupOldInboxMessages() {
        LocalDateTime threshold = LocalDateTime.now().minusDays(retentionDays);

        int deletedCount = inboxMessageRepository.deleteOldCompletedMessages(threshold);

        log.info("[INBOX CLEANUP] Deleted {} old completed messages older than {}",
                deletedCount, threshold);
    }
}
