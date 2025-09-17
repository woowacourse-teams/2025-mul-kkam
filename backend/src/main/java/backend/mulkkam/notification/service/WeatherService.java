package backend.mulkkam.notification.service;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_FORECAST_DATE;
import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_FORECAST_TARGET_DATE;
import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_AVERAGE_TEMPERATURE;

import backend.mulkkam.averageTemperature.domain.AverageTemperature;
import backend.mulkkam.averageTemperature.repository.AverageTemperatureRepository;
import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.intake.dto.OpenWeatherResponse;
import backend.mulkkam.notification.domain.City;
import backend.mulkkam.notification.domain.CityDate;
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

    private final WeatherClient weatherClient;
    private final AverageTemperatureRepository averageTemperatureRepository;

    @Scheduled(cron = "0 0 19 * * *")
    @Transactional
    public void saveTomorrowAverageTemperature() {
        notifyAdditionalIntakeByStoredWeather(CityDate.now(City.SEOUL));
    }

    public void notifyAdditionalIntakeByStoredWeather(CityDate cityDate) {
        CityDate tomorrowCityDate = new CityDate(cityDate.city(), cityDate.localDate().plusDays(1));

        double averageTemperatureForCityDate = getAverageTemperatureForCityDate(tomorrowCityDate);
        AverageTemperature averageTemperature = new AverageTemperature(tomorrowCityDate, averageTemperatureForCityDate);
        averageTemperatureRepository.save(averageTemperature);
    }

    public double getAverageTemperatureForCityDate(CityDate cityDate) {
        validateForecastTargetDateRange(cityDate);

        OpenWeatherResponse weatherOfFourDays = weatherClient.getFourDayWeatherForecast(cityDate.getCityCode());
        double averageTemperatureForDate = computeAverageTemperatureForDate(weatherOfFourDays, cityDate.localDate());

        return convertFromKelvinToCelsius(averageTemperatureForDate);
    }

    public AverageTemperature getAverageTemperature(LocalDate todayInSeoul) {
        return averageTemperatureRepository.findByDate(todayInSeoul)
                .orElseThrow(() -> new CommonException(NOT_FOUND_AVERAGE_TEMPERATURE));
    }

    private void validateForecastTargetDateRange(CityDate cityDate) {
        LocalDate targetDate = cityDate.localDate();
        LocalDate now = CityDate.now(cityDate.city()).localDate();

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
