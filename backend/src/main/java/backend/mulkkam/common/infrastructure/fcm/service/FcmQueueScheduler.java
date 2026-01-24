package backend.mulkkam.common.infrastructure.fcm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class FcmQueueScheduler {

    private final FcmQueueService fcmQueueService;

    @Scheduled(fixedDelayString = "${fcm.queue.poll-interval-ms:5000}")
    public void pollAndSend() {
        try {
            fcmQueueService.processPendingQueue();
        } catch (Exception e) {
            log.error("[FCM QUEUE SCHEDULER ERROR] message={}, type={}",
                    e.getMessage(),
                    e.getClass().getSimpleName());
        }
    }
}
