package backend.mulkkam.notification.controller;

import backend.mulkkam.notification.service.ReminderScheduleService;
import backend.mulkkam.notification.service.SuggestionNotificationService;
import io.swagger.v3.oas.annotations.Hidden;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/weather")
public class WeatherController { // 백엔드 테스트용 controller (삭제 예정)

    private final SuggestionNotificationService suggestionNotificationService;
    private final ReminderScheduleService reminderScheduleService;

    @Hidden
    @PostMapping
    public ResponseEntity<Void> create() {
        suggestionNotificationService.notifyAdditionalWaterIntakeByWeather();
        return ResponseEntity.ok().build();
    }

    @Hidden
    @PostMapping("/not-weather/remind")
    public ResponseEntity<Void> create2(@RequestParam(required = false) LocalDateTime clientTime) {
        if (clientTime == null) {
            clientTime = LocalDateTime.now();
        }
        reminderScheduleService.executeReminderNotification(clientTime);
        return ResponseEntity.ok().build();
    }
}
