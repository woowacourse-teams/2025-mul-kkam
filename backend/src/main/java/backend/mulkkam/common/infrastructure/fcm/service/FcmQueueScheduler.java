package backend.mulkkam.common.infrastructure.fcm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class FcmQueueScheduler {

    private final FcmQueueService fcmQueueService;

    @Scheduled(fixedDelayString = "${fcm.queue.poll-interval-ms:5000}")
    public void pollAndSend() {
        fcmQueueService.processPendingQueue();
    }
}
