package backend.mulkkam.notification.service;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_FORECAST_DATE;
import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_FORECAST_TARGET_DATE;
import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_AVERAGE_TEMPERATURE;

import backend.mulkkam.averageTemperature.domain.AverageTemperature;
import backend.mulkkam.averageTemperature.repository.AverageTemperatureRepository;
import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.intake.dto.OpenWeatherResponse;
import backend.mulkkam.notification.domain.City;
import backend.mulkkam.notification.domain.CityDateTime;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class WeatherService {

    private static final int AVAILABLE_DATE_RANGE_FOR_FORECAST = 5;
    private static final String DAILY_7PM_CRON = "0 0 19 * * *";

    private final WeatherClient weatherClient;
    private final AverageTemperatureRepository averageTemperatureRepository;

    @Scheduled(cron = DAILY_7PM_CRON)
    @Transactional
    public void saveTomorrowAverageTemperature() {
        notifyAdditionalIntakeByStoredWeather(CityDateTime.now(City.SEOUL));
    }

    public void notifyAdditionalIntakeByStoredWeather(CityDateTime cityDateTime) {
        CityDateTime tomorrowCityDateTime = new CityDateTime(cityDateTime.city(), cityDateTime.localDateTime().plusDays(1));

        double averageTemperatureForCityDate = getAverageTemperatureForCityDate(tomorrowCityDateTime);
        AverageTemperature averageTemperature = new AverageTemperature(tomorrowCityDateTime, averageTemperatureForCityDate);
        averageTemperatureRepository.save(averageTemperature);
    }

    public double getAverageTemperatureForCityDate(CityDateTime cityDateTime) {
        validateForecastTargetDateRange(cityDateTime);

        OpenWeatherResponse weatherOfFourDays = weatherClient.getFourDayWeatherForecast(cityDateTime.getCityCode());
        double averageTemperatureForDate = computeAverageTemperatureForDate(weatherOfFourDays, cityDateTime.getLocalDate());

        return convertFromKelvinToCelsius(averageTemperatureForDate);
    }

    public AverageTemperature getAverageTemperature(CityDateTime cityDateTime) {
        return averageTemperatureRepository.findByCityAndDate(cityDateTime.city(), cityDateTime.getLocalDate())
                .orElseThrow(() -> new CommonException(NOT_FOUND_AVERAGE_TEMPERATURE));
    }

    private void validateForecastTargetDateRange(CityDateTime cityDateTime) {
        LocalDate targetDate = cityDateTime.getLocalDate();
        LocalDate now = CityDateTime.now(cityDateTime.city()).getLocalDate();

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
