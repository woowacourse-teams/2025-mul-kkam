package backend.mulkkam.infrastructure;

import backend.mulkkam.member.dto.response.KakaoUserInfo;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class KakaoRestClient {

    private static final String URL = "https://kapi.kakao.com/v2";

    private final RestClient restClient;

    public KakaoRestClient() {
        this.restClient = RestClient.builder()
                .baseUrl(URL)
                .defaultHeader(
                        HttpHeaders.CONTENT_TYPE,
                        MediaType.APPLICATION_JSON_VALUE
                )
                .build();
    }

    public KakaoUserInfo getUserInfo(String accessToken) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/user/me")
                        .build())
                .header("Authorization", "Bearer " + accessToken)
                .header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")
                .retrieve()
                .body(KakaoUserInfo.class);
    }
}
