package backend.mulkkam.intake.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;

public record OpenWeatherResponse(
        @JsonProperty("list")
        List<ForecastEntry> forecastEntries,

        @JsonProperty("city")
        CityInfo city
) {
    public record ForecastEntry(
            @JsonProperty("main")
            TemperatureInfo info,

            @JsonProperty("dt_txt")
            @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            LocalDateTime dtTxt
    ) {
        public record TemperatureInfo(
                double temp
        ) {
        }
    }

    public record CityInfo(
            @JsonProperty("timezone")
            int timezone
    ) {
    }
}
