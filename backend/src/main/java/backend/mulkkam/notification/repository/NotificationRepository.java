package backend.mulkkam.notification.repository;

import backend.mulkkam.member.domain.Member;
import backend.mulkkam.notification.domain.Notification;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("""
            SELECT n
            FROM Notification n
            LEFT JOIN fetch SuggestionNotification s
            ON n.id = s.id
            WHERE n.id < :lastId
              AND n.createdAt >= :limitStartDateTime
              AND n.member.id = :memberId
            ORDER BY n.id DESC
            """)
    List<Notification> findByCursor(
            Long memberId,
            Long lastId,
            LocalDateTime limitStartDateTime,
            Pageable pageable
    );

    @Query("""
            SELECT n
            FROM Notification n
            LEFT JOIN fetch SuggestionNotification s
            ON n.id = s.id
            WHERE n.createdAt >= :limitStartDateTime
            AND n.member.id = :memberId
            ORDER BY n.id DESC
            """)
    List<Notification> findLatest(
            Long memberId,
            LocalDateTime limitStartDateTime,
            Pageable pageable
    );

    void deleteByMember(Member member);

    long countByIsReadFalseAndMemberId(Long memberId);

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
