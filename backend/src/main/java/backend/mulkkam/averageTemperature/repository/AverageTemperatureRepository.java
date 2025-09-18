package backend.mulkkam.averageTemperature.repository;

import backend.mulkkam.averageTemperature.domain.AverageTemperature;
import backend.mulkkam.notification.domain.City;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AverageTemperatureRepository extends JpaRepository<AverageTemperature, Long> {

    Optional<AverageTemperature> findByCityAndDate(City city, LocalDate localDate);
}
