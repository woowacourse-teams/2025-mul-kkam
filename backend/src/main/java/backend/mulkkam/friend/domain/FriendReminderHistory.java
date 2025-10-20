package backend.mulkkam.friend.domain;

import backend.mulkkam.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(
        name = "uk_sender_recipient_date",
        columnNames = {"sender_id", "recipient_id", "quota_date"}))
@Entity
public class FriendReminderHistory extends BaseEntity {

    private static final short MAX_TRY_COUNT = 10;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="sender_id", nullable=false)
    private Long senderId;

    @Column(name="recipient_id", nullable=false)
    private Long recipientId;

    @Column(name="quota_date", nullable=false)
    private LocalDate quotaDate;

    @Column(name="remaining", nullable=false)
    private short remaining;

    public FriendReminderHistory(Long id, Long senderId, Long recipientId, LocalDate quotaDate) {
        this.id = id;
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.quotaDate = quotaDate;
        this.remaining = MAX_TRY_COUNT;
    }

    public FriendReminderHistory(Long senderId, Long recipientId, LocalDate quotaDate) {
        this(null, senderId, recipientId, quotaDate);
    }
}
