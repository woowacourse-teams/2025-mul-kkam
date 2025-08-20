package backend.mulkkam.common.domain;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@MappedSuperclass
public class BaseEntity {

    @Column(name = "created_at")
    protected LocalDateTime createdAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public BaseEntity() {
        this.createdAt = LocalDateTime.now();
        this.deletedAt = null;
    }
}
