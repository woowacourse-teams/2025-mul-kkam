package backend.mulkkam.averageTemperature.serivce;

import backend.mulkkam.notification.service.WeatherService;
import backend.mulkkam.intakenotification.IntakeNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScheduleService {

    private final WeatherService weatherService;
    private final IntakeNotificationService intakeNotificationService;

    @Scheduled(cron = "0 0 19 * * *")
    public void saveTomorrowAverageTemperature() {
        weatherService.saveTomorrowAverageTemperature();
    }

    @Scheduled(cron = "0 0 8 * * *")
    public void notifyAdditionalWaterIntakeByWeather() {
        intakeNotificationService.notifyAdditionalIntakeByStoredWeather();
    }

    @Scheduled(cron = "0 0 14 * * *")
    @Scheduled(cron = "0 0 19 * * *")
    public void notifyRemindNotification() {
        intakeNotificationService.notifyRemindNotification();
    }
}
