package backend.mulkkam.averageTemperature.domain;

import backend.mulkkam.notification.domain.City;
import backend.mulkkam.notification.domain.CityDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private City city;

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
