package backend.mulkkam.messaging.inbox.repository;

import backend.mulkkam.messaging.inbox.domain.InboxMessage;
import java.time.LocalDateTime;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Profile("consumer")
public interface InboxMessageRepository extends JpaRepository<InboxMessage, String> {

    boolean existsByMessageId(String messageId);

    @Modifying
    @Query("DELETE FROM InboxMessage i WHERE i.createdAt < :threshold AND i.status = 'COMPLETED'")
    int deleteOldCompletedMessages(@Param("threshold") LocalDateTime threshold);
}
