package backend.mulkkam.intake.infrastructure;

import backend.mulkkam.intake.dto.OpenWeatherResponse;
import backend.mulkkam.notification.service.WeatherClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class WeatherRestClient implements WeatherClient {

    private static final String URL = "https://api.openweathermap.org";

    @Value("${open-weather.api.key}")
    private String apiKey;

    private final RestClient restClient;

    public WeatherRestClient() {
        this.restClient = RestClient.builder()
                .baseUrl(URL)
                .defaultHeader(
                        HttpHeaders.CONTENT_TYPE,
                        MediaType.APPLICATION_JSON_VALUE
                )
                .build();
    }

    @Override
    public OpenWeatherResponse getFourDayWeatherForecast(String cityCode) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/data/2.5/forecast")
                        .queryParam("id", cityCode)
                        .queryParam("appid", apiKey)
                        .build())
                .retrieve()
                .body(OpenWeatherResponse.class);
    }
}
