package backend.mulkkam.notification.controller;

import backend.mulkkam.notification.service.NotificationService;
import backend.mulkkam.notification.service.SuggestionNotificationService;
import backend.mulkkam.notification.service.WeatherService;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/weather")
public class WeatherController { // 백엔드 테스트용 controller (삭제 예정)

    private final WeatherService weatherService;
    private final NotificationService notificationService;
    private final SuggestionNotificationService suggestionNotificationService;

    @Hidden
    @PostMapping
    public ResponseEntity<Void> create() {
        suggestionNotificationService.notifyAdditionalWaterIntakeByWeather();
        return ResponseEntity.ok().build();
    }

    @Hidden
    @PostMapping("/not-weather/remind")
    public ResponseEntity<Void> create2() {
        notificationService.notifyRemindNotification();
        return ResponseEntity.ok().build();
    }
}
