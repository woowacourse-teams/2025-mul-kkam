package backend.mulkkam.outboxnotification.domain;

import backend.mulkkam.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OutboxNotification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private String token;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String body;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(unique = true, nullable = false)
    private String dedupeKey;

    private int attemptCount;

    private String lastError;

    private LocalDateTime nextAttemptAt;

    public enum Status {
        READY, SENDING, SENT, RETRY, FAIL
    }

    public OutboxNotification(
            String type,
            Long memberId,
            String token,
            String title,
            String body,
            Status status,
            String dedupeKey,
            int attemptCount,
            String lastError,
            LocalDateTime nextAttemptAt
    ) {
        this.type = type;
        this.memberId = memberId;
        this.token = token;
        this.title = title;
        this.body = body;
        this.status = status;
        this.dedupeKey = dedupeKey;
        this.attemptCount = attemptCount;
        this.lastError = lastError;
        this.nextAttemptAt = nextAttemptAt;
    }
}
