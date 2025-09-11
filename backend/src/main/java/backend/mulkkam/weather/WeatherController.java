package backend.mulkkam.weather;

import backend.mulkkam.intakenotification.IntakeNotificationService;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/weather")
public class WeatherController {

    private final IntakeNotificationService intakeNotificationService;

    @Hidden
    @PostMapping
    public ResponseEntity<Void> create() {
        intakeNotificationService.notifyAdditionalIntakeByStoredWeather();
        return ResponseEntity.ok().build();
    }
}
