package backend.mulkkam.avgTemperature.repository;

import backend.mulkkam.avgTemperature.domain.AverageTemperature;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AverageTemperatureRepository extends JpaRepository<AverageTemperature, Long> {

    Optional<AverageTemperature> findByDate(LocalDate date);
}
