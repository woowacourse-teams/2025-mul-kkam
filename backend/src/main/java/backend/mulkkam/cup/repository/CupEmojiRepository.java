package backend.mulkkam.cup.repository;

import backend.mulkkam.cup.domain.CupEmoji;
import backend.mulkkam.cup.domain.vo.CupEmojiUrl;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CupEmojiRepository extends JpaRepository<CupEmoji, Long>  {

    Optional<CupEmoji> findByUrl(CupEmojiUrl cupEmojiUrl);
    List<CupEmoji> findAllByUrlIn(Collection<CupEmojiUrl> urls);
}
