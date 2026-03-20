package backend.mulkkam.friend.domain;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.NOT_ALLOWED_SELF_REMINDER;

import backend.mulkkam.common.domain.BaseEntity;
import backend.mulkkam.common.exception.CommonException;
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

    private static final short INIT_REMAINING_VALUE = 10;

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

    private FriendReminderHistory(Long id, Long senderId, Long recipientId, LocalDate quotaDate) {
        if (senderId.equals(recipientId)) {
            throw new CommonException(NOT_ALLOWED_SELF_REMINDER);
        }
        this.id = id;
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.quotaDate = quotaDate;
        this.remaining = INIT_REMAINING_VALUE;
    }

    public FriendReminderHistory(Long senderId, Long recipientId, LocalDate quotaDate) {
        this(null, senderId, recipientId, quotaDate);
    }
}
