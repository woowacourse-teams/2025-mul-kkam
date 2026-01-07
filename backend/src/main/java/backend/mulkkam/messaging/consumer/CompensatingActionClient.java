package backend.mulkkam.messaging.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@Profile("consumer")
public class CompensatingActionClient {

    private final RestTemplate restTemplate;
    private final String apiServerBaseUrl;

    public CompensatingActionClient(
            RestTemplate restTemplate,
            @Value("${consumer.api-server.base-url:http://localhost:8080}") String apiServerBaseUrl
    ) {
        this.restTemplate = restTemplate;
        this.apiServerBaseUrl = apiServerBaseUrl;
    }

    @Retryable(
            retryFor = RestClientException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public void requestTokenDeletion(Long memberId, String token) {
        String url = apiServerBaseUrl + "/internal/devices/invalid-token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        TokenDeletionRequest request = new TokenDeletionRequest(memberId, token);
        HttpEntity<TokenDeletionRequest> entity = new HttpEntity<>(request, headers);

        try {
            restTemplate.postForEntity(url, entity, Void.class);
            log.info("[TOKEN DELETION REQUESTED] memberId={}, token={}",
                    memberId, token.substring(0, Math.min(10, token.length())) + "...");
        } catch (RestClientException e) {
            log.error("[TOKEN DELETION FAILED] memberId={}, error={}",
                    memberId, e.getMessage());
            throw e;
        }
    }

    public record TokenDeletionRequest(Long memberId, String token) {
    }
}
