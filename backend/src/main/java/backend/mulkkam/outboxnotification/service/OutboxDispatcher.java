package backend.mulkkam.outboxnotification.service;

import backend.mulkkam.common.infrastructure.fcm.service.FcmClient;
import backend.mulkkam.outboxnotification.domain.OutboxNotification;
import backend.mulkkam.outboxnotification.repository.OutboxNotificationRepository;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.SendResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OutboxDispatcher {

    private final OutboxNotificationRepository outboxRepository;
    private final FcmClient fcmClient;

    private static final int FETCH_SIZE = 200;
    private static final int MULTICAST_SIZE = 500;

    @Scheduled(fixedDelay = 5000)
    public void dispatch() {

        // SKIP LOCKED로 200개 가져오기
        List<OutboxNotification> jobs =
                outboxRepository.fetchReadyForSend(FETCH_SIZE);

        if (jobs.isEmpty()) {
            return;
        }

        // 상태 → SENDING (+ attemptCount++)
        jobs.forEach(job -> outboxRepository.markSending(job.getId()));

        List<String> tokens = jobs.stream()
                .map(OutboxNotification::getToken)
                .toList();

        String title = jobs.getFirst().getTitle();
        String body = jobs.getFirst().getBody();

        List<List<Integer>> chunks = chunkIndices(tokens.size(), MULTICAST_SIZE);

        for (List<Integer> chunk : chunks) {

            List<String> chunkTokens = chunk.stream()
                    .map(tokens::get)
                    .toList();

            BatchResponse response =
                    fcmClient.sendMulticast(title, body, "REMIND", chunkTokens);

            handleFcmResponse(jobs, chunk, response);
        }
    }

    private void handleFcmResponse(
            List<OutboxNotification> jobs,
            List<Integer> chunkIndexes,
            BatchResponse response
    ) {
        List<SendResponse> results = response.getResponses();

        for (int j = 0; j < chunkIndexes.size(); j++) {

            int idx = chunkIndexes.get(j);
            OutboxNotification job = jobs.get(idx);

            SendResponse result = results.get(j);

            if (result.isSuccessful()) {
                outboxRepository.markSent(job.getId());
                continue;
            }

            String error = result.getException().getMessagingErrorCode().name();

            if (isPermanentError(error)) {
                outboxRepository.markFail(
                        job.getId(),
                        error
                );
            } else {
                outboxRepository.markRetryOrFail(
                        job.getId(),
                        nextBackoffTime(job.getAttemptCount()),
                        error
                );
            }
        }
    }

    private boolean isPermanentError(String error) {
        return Set.of(
                "UNREGISTERED",
                "INVALID_ARGUMENT",
                "INVALID_REGISTRATION_TOKEN",
                "MISMATCH_SENDER_ID",
                "THIRD_PARTY_AUTH_ERROR"
        ).contains(error);
    }

    private LocalDateTime nextBackoffTime(int attempt) {
        long sec = Math.min(60, (long) (2 * Math.pow(2, attempt)));
        return LocalDateTime.now().plusSeconds(sec);
    }

    private List<List<Integer>> chunkIndices(int size, int chunkSize) {
        List<List<Integer>> result = new ArrayList<>();
        for (int i = 0; i < size; i += chunkSize) {
            List<Integer> chunk = new ArrayList<>();
            for (int j = i; j < Math.min(i + chunkSize, size); j++) {
                chunk.add(j);
            }
            result.add(chunk);
        }
        return result;
    }
}
