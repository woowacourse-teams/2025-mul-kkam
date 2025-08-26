package backend.mulkkam.version.repository;

import backend.mulkkam.version.domain.AppMinimumVersion;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AppMinimumVersionRepository extends JpaRepository<AppMinimumVersion, Long> {

    @Query(value = """
                SELECT a FROM AppMinimumVersion a
                ORDER BY a.updatedAt DESC 
                LIMIT 1
            """)
    Optional<AppMinimumVersion> findLatestAppMinimumVersion();
}
