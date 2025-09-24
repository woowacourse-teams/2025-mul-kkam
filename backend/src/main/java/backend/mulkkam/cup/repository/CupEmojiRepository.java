package backend.mulkkam.cup.repository;

import backend.mulkkam.cup.domain.CupEmoji;
import backend.mulkkam.cup.domain.EmojiCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.Query;

public interface CupEmojiRepository extends JpaRepository<CupEmoji, Long>  {

    List<CupEmoji> findAllByCodeIn(Collection<EmojiCode> defaultEmojiCodes);

    @Query("""
        SELECT c
        FROM CupEmoji c
        WHERE c.deletedAt IS NULL
        ORDER BY
            CASE
                WHEN :highestPriorityEmojiCode IS NOT NULL AND c.code = :highestPriorityEmojiCode THEN 0
                ELSE 1
            END,
            c.code ASC NULLS LAST
    """)
    List<CupEmoji> findAllOrderByCode(EmojiCode highestPriorityEmojiCode);
}
