package backend.mulkkam.cup.repository;

import backend.mulkkam.cup.domain.CupEmoji;
import backend.mulkkam.cup.domain.EmojiCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface CupEmojiRepository extends JpaRepository<CupEmoji, Long>  {

    List<CupEmoji> findAllByCodeIn(Collection<EmojiCode> defaultEmojiCodes);
}
