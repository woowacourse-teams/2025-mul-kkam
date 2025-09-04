package backend.mulkkam.weather;

import backend.mulkkam.intake.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/weather")
public class WeatherController {

    private final WeatherService weatherService;

    @PostMapping
    public ResponseEntity<Void> create() {
        weatherService.notifyAdditionalIntakeByStoredWeather();
        return ResponseEntity.ok().build();
    }
}
