package backend.mulkkam.averageTemperature.serivce;

import backend.mulkkam.intake.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScheduleService {

    private final WeatherService weatherService;

    @Scheduled(cron = "0 */2 * * * *")
    public void saveTomorrowAverageTemperature() {
        weatherService.saveTomorrowAverageTemperature();
    }

    @Scheduled(cron = "0 0 8 * * *")
    public void notifyAdditionalWaterIntakeByWeather() {
        weatherService.notifyAdditionalIntakeByStoredWeather();
    }
}
