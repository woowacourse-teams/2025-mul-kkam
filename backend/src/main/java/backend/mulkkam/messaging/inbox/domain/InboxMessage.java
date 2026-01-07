package backend.mulkkam.messaging.inbox.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "inbox_message",
        indexes = {
                @Index(name = "idx_inbox_created_at", columnList = "createdAt"),
                @Index(name = "idx_inbox_status", columnList = "status")
        }
)
public class InboxMessage {

    @Id
    @Column(length = 36)
    private String messageId; // UUID - Natural Key로 중복 방지

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime processedAt;

    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    public enum Status {
        RECEIVED,    // 수신됨
        PROCESSING,  // 처리 중
        COMPLETED,   // 완료
        FAILED       // 실패 (DLQ로 이동)
    }

    public static InboxMessage create(String messageId, Long memberId, String token) {
        return new InboxMessage(
                messageId,
                memberId,
                token,
                Status.RECEIVED,
                LocalDateTime.now(),
                null,
                null
        );
    }

    public void markProcessing() {
        this.status = Status.PROCESSING;
    }

    public void markCompleted() {
        this.status = Status.COMPLETED;
        this.processedAt = LocalDateTime.now();
    }

    public void markFailed(String errorMessage) {
        this.status = Status.FAILED;
        this.processedAt = LocalDateTime.now();
        this.errorMessage = errorMessage;
    }
}
