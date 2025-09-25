package backend.mulkkam.notification.repository;

import backend.mulkkam.member.domain.Member;
import backend.mulkkam.notification.domain.Notification;
import backend.mulkkam.notification.dto.ReadNotificationRow;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("""
        SELECT new backend.mulkkam.notification.dto.ReadNotificationRow(
            n.id,
            n.createdAt,
            n.content,
            n.notificationType,
            n.isRead,
            s.recommendedTargetAmount,
            s.applyTargetAmount
        )
        FROM Notification n
        LEFT JOIN SuggestionNotification s ON s.id = n.id
        WHERE n.createdAt >= :limitStartDateTime
          AND n.member.id = :memberId
        ORDER BY n.id DESC
    """)
    List<ReadNotificationRow> findLatestRows(
            Long memberId,
            LocalDateTime limitStartDateTime,
            Pageable pageable
    );

    @Query("""
        SELECT new backend.mulkkam.notification.dto.ReadNotificationRow(
            n.id,
            n.createdAt,
            n.content,
            n.notificationType,
            n.isRead,
            s.recommendedTargetAmount,
            s.applyTargetAmount
        )
        FROM Notification n
        LEFT JOIN SuggestionNotification s ON s.id = n.id
        WHERE n.id < :lastId
          AND n.createdAt >= :limitStartDateTime
          AND n.member.id = :memberId
        ORDER BY n.id DESC
    """)
    List<ReadNotificationRow> findByCursorRows(
            Long memberId,
            Long lastId,
            LocalDateTime limitStartDateTime,
            Pageable pageable
    );

    void deleteByMember(Member member);

    @Modifying(clearAutomatically = true)
    @Query("""
        UPDATE Notification n
           SET n.isRead = true
         WHERE n.id IN :ids
    """)
    int markReadInBulk(List<Long> ids);

    @Query("""
        SELECT COUNT(n)
        FROM Notification n
        WHERE n.createdAt >= :limitStartDateTime
          AND n.member.id = :memberId
          AND n.isRead = false
        """)
    long countByIsReadFalseAndMemberId(Long memberId, LocalDateTime limitStartDateTime);

    List<Notification> findAllByMember(Member member);

    @Query("""
            SELECT n
            FROM Notification n
            JOIN fetch n.member
            WHERE n.id = :id
            """
    )
    Optional<Notification> findByIdWithMember(Long id);
}
