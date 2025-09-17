package backend.mulkkam.notification.controller;

import backend.mulkkam.notification.domain.City;
import backend.mulkkam.notification.domain.CityDateTime;
import backend.mulkkam.notification.service.NotificationService;
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

    @Hidden
    @PostMapping
    public ResponseEntity<Void> create() {
        weatherService.notifyAdditionalIntakeByStoredWeather(CityDateTime.now(City.SEOUL));
        return ResponseEntity.ok().build();
    }

    @Hidden
    @PostMapping("/not-weather/remind")
    public ResponseEntity<Void> create2() {
        notificationService.notifyRemindNotification();
        return ResponseEntity.ok().build();
    }
}
