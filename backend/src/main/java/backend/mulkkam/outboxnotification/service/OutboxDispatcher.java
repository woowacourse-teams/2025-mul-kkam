package backend.mulkkam.outboxnotification.service;

import backend.mulkkam.common.infrastructure.fcm.service.FcmClient;
import backend.mulkkam.outboxnotification.domain.OutboxNotification;
import backend.mulkkam.outboxnotification.repository.OutboxNotificationRepository;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.MessagingErrorCode;
import com.google.firebase.messaging.SendResponse;
import jakarta.transaction.Transactional;
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

    private static final int MULTICAST_SIZE = 500;

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void dispatch() {
        List<OutboxNotification> jobs =
                outboxRepository.fetchReadyForSend(MULTICAST_SIZE);

        if (jobs.isEmpty()) {
            return;
        }
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

            MessagingErrorCode errorCode = result.getException().getMessagingErrorCode();
            String error = (errorCode != null) ? errorCode.name() : "UNKNOWN";
            String reason = (result.getException().getMessage() != null)
                    ? result.getException().getMessage()
                    : error;

            if (isPermanentError(error)) {
                outboxRepository.markFail(
                        job.getId(),
                        reason
                );
            } else {
                outboxRepository.markRetryOrFail(
                        job.getId(),
                        nextBackoffTime(job.getAttemptCount()),
                        reason
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
