package backend.mulkkam.notification.service;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_FORECAST_DATE;
import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_FORECAST_TARGET_DATE;
import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_AVERAGE_TEMPERATURE;

import backend.mulkkam.averageTemperature.domain.AverageTemperature;
import backend.mulkkam.averageTemperature.repository.AverageTemperatureRepository;
import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.intake.dto.OpenWeatherResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class WeatherService {

    private static final String SEOUL_ZONE_ID = "Asia/Seoul";
    private static final String SEOUL_CITY_CODE = "1835847";
    private static final int AVAILABLE_DATE_RANGE_FOR_FORECAST = 5;

    private final WeatherClient weatherClient;
    private final AverageTemperatureRepository averageTemperatureRepository;

    @Scheduled(cron = "0 0 19 * * *")
    @Transactional
    public void saveTomorrowAverageTemperature() {
        ZoneId seoulZone = ZoneId.of(SEOUL_ZONE_ID);
        LocalDate todayDateInSeoul = ZonedDateTime.now(seoulZone).toLocalDate();
        LocalDate tomorrowDateInSeoul = todayDateInSeoul.plusDays(1);

        double averageTemperatureForDate = getAverageTemperatureForDate(tomorrowDateInSeoul);
        AverageTemperature averageTemperature = new AverageTemperature(tomorrowDateInSeoul, averageTemperatureForDate);
        averageTemperatureRepository.save(averageTemperature);
    }

    public double getAverageTemperatureForDate(LocalDate targetDate) {
        validateTargetDate(targetDate);

        OpenWeatherResponse weatherOfFourDays = weatherClient.getFourDayWeatherForecast(SEOUL_CITY_CODE);
        double averageTemperatureForDate = computeAverageTemperatureForDate(weatherOfFourDays, targetDate);

        return convertFromKelvinToCelsius(averageTemperatureForDate);
    }

    public AverageTemperature getAverageTemperature(LocalDate todayInSeoul) {
        return averageTemperatureRepository.findByDate(todayInSeoul)
                .orElseThrow(() -> new CommonException(NOT_FOUND_AVERAGE_TEMPERATURE));
    }

    private void validateTargetDate(LocalDate targetDate) {
        LocalDate now = LocalDate.now(ZoneId.of(SEOUL_ZONE_ID));

        if (!targetDate.isAfter(now)) {
            throw new CommonException(INVALID_FORECAST_TARGET_DATE);
        }

        if (targetDate.isAfter(now.plusDays(AVAILABLE_DATE_RANGE_FOR_FORECAST))) {
            throw new CommonException(INVALID_FORECAST_TARGET_DATE);
        }
    }

    private double computeAverageTemperatureForDate(
            OpenWeatherResponse response,
            LocalDate targetDate
    ) {
        int offsetSeconds = response.city().timezone();
        Duration offset = Duration.ofSeconds(offsetSeconds);

        return response.forecastEntries().stream()
                .filter(entry -> isSameDate(entry.dateTime(), targetDate, offset))
                .mapToDouble(entry -> entry.temperatureInfo().temperature())
                .average()
                .orElseThrow(() -> new CommonException(INVALID_FORECAST_DATE));
    }

    private double convertFromKelvinToCelsius(double temperatureAsKelvin) {
        return temperatureAsKelvin - 273.15;
    }

    private boolean isSameDate(
            LocalDateTime dateTime,
            LocalDate targetDate,
            Duration offset
    ) {
        return dateTime.plusSeconds(offset.getSeconds())
                .toLocalDate()
                .equals(targetDate);
    }
}
