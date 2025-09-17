package backend.mulkkam.averageTemperature.domain;

import backend.mulkkam.notification.domain.City;
import backend.mulkkam.notification.domain.CityDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class AverageTemperature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private City city; // TODO 2025. 9. 17. 15:24: 적절한 지 -> flyway 처리

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private double temperature;

    public AverageTemperature(
            CityDateTime cityDateTime,
            double temperature
    ) {
        this.city = cityDateTime.city();
        this.date = cityDateTime.getLocalDate();
        this.temperature = temperature;
    }
}
