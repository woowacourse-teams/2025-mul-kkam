package backend.mulkkam.friend.repository;

import backend.mulkkam.friend.domain.FriendReminderHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface FriendReminderHistoryRepository extends JpaRepository<FriendReminderHistory, Long> {

    Optional<FriendReminderHistory> findBySenderIdAndRecipientIdAndQuotaDate(Long senderId, Long recipientId, LocalDate quotaDate);

    @Modifying
    @Query("""
           UPDATE FriendReminderHistory h
           SET h.remaining = h.remaining - 1
           WHERE h.id = :id AND h.remaining > 0
           """)
    int tryReduceRemaining(@Param("id") Long id);

    @Modifying
    @Query(value = """
            INSERT INTO friend_reminder_history(sender_id, recipient_id, quota_date, remaining)
            VALUES (:senderId, :friendId, :quotaDate, :initRemaining)
            ON DUPLICATE KEY UPDATE remaining = friend_reminder_history.remaining
            """, nativeQuery = true)
    void createIfAbsent(
            @Param("senderId") Long senderId,
            @Param("friendId") Long friendId,
            @Param("quotaDate") LocalDate quotaDate,
            @Param("initRemaining") short initRemaining
    );
}
