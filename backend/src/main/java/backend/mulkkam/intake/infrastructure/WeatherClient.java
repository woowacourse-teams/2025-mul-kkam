package backend.mulkkam.intake.infrastructure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

public class WeatherClient {

    private static final String URL = "https://api.openweathermap.org";

    @Value("${open-weather.api.key}")
    private String apiKey;

    private final RestClient restClient;

    public WeatherClient() {
        this.restClient = RestClient.builder()
                .baseUrl(URL)
                .defaultHeader(
                        HttpHeaders.CONTENT_TYPE,
                        MediaType.APPLICATION_JSON_VALUE
                )
                .build();
    }

    public String getCurrentWeather(String city) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/data/2.5/forecast")
                        .queryParam("id", city)
                        .queryParam("appid", apiKey)
                        .build())
                .retrieve()
                .body(String.class);
    }
}
