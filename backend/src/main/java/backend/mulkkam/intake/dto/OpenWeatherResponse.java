package backend.mulkkam.intake.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;

public record OpenWeatherResponse(
        @JsonProperty("list")
        List<ForecastEntry> forecastEntries,

        CityInfo city
) {
    public record ForecastEntry(
            @JsonProperty("main")
            TemperatureInfo temperatureInfo,

            @JsonProperty("dt_txt")
            @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            LocalDateTime dateTime
    ) {
        public record TemperatureInfo(
                @JsonProperty("temp")
                double temperature
        ) {
        }
    }

    public record CityInfo(
            int timezone
    ) {
    }
}
