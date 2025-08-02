package backend.mulkkam.intake.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;

public record OpenWeatherResponse(
        @JsonProperty("list")
        List<ForecastEntry> forecastEntries
) {
    public record ForecastEntry(
            TemperatureInfo info,

            @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            LocalDateTime dtTxt
    ) {
        public record TemperatureInfo(
                double temp
        ) {

        }
    }
}
