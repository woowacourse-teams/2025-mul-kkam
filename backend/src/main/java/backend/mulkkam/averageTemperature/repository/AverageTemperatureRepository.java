package backend.mulkkam.averageTemperature.repository;

import backend.mulkkam.averageTemperature.domain.AverageTemperature;
import backend.mulkkam.notification.domain.City;
import java.lang.ScopedValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface AverageTemperatureRepository extends JpaRepository<AverageTemperature, Long> {

    Optional<AverageTemperature> findByDate(LocalDate date);

    Optional<AverageTemperature> findByCityAndDate(City city, LocalDate localDate);
}
