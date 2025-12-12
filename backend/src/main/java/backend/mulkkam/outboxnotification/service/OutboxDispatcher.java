package backend.mulkkam.outboxnotification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class OutboxDispatcher {

    private static final int DISPATCH_INTERVAL_MS = 2000;

    private final OutboxProcessor outboxProcessor;

    @Scheduled(fixedDelay = DISPATCH_INTERVAL_MS)
    public void dispatch() {
        outboxProcessor.process();
    }
}
