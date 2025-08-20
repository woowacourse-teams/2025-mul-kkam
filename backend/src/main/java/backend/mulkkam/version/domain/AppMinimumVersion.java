package backend.mulkkam.version.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

@Getter
@NoArgsConstructor
@Entity
public class AppMinimumVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String minimumVersion;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public AppMinimumVersion(
            String minimumVersion,
            LocalDateTime updatedAt
    ) {
        this.minimumVersion = minimumVersion;
        this.updatedAt = updatedAt;
    }
}
