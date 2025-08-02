package backend.mulkkam.intake.service;

import backend.mulkkam.intake.dto.OpenWeatherResponse;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class WeatherService {

    private static final String SEOUL_CITY_CODE = "1835847";

    private final WeatherClient weatherClient;

    public double getAverageTemperatureForDate(LocalDate targetDate) {
        OpenWeatherResponse weatherOfFourDays = weatherClient.getFourDayWeatherForecast(SEOUL_CITY_CODE);
        double averageTemperatureForDate = computeAverageTemperatureForDate(weatherOfFourDays, targetDate);

        return convertFromKelvinToCelsius(averageTemperatureForDate);
    }

    private double computeAverageTemperatureForDate(OpenWeatherResponse response, LocalDate targetDate) {
        return response.forecastEntries().stream()
                .filter(entry -> entry.dtTxt()
                        .atZone(ZoneOffset.UTC)
                        .withZoneSameInstant(ZoneId.of("Asia/Seoul"))
                        .toLocalDate()
                        .equals(targetDate))
                .mapToDouble(entry -> entry.info().temp())
                .average()
                .orElseThrow(() -> new IllegalArgumentException("해당 날짜의 기온 데이터가 없습니다."));
    }

    private double convertFromKelvinToCelsius(double temperatureAsKelvin) {
        return temperatureAsKelvin - 273.15;
    }

}
