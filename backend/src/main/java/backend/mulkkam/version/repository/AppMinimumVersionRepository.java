package backend.mulkkam.version.repository;

import backend.mulkkam.version.domain.AppMinimumVersion;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppMinimumVersionRepository extends JpaRepository<AppMinimumVersion, Long> {

    Optional<AppMinimumVersion> findFirstByOrderByUpdatedAtDesc();
}
