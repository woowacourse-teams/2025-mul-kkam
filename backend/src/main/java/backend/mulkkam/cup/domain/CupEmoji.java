package backend.mulkkam.cup.domain;

import backend.mulkkam.common.domain.BaseEntity;
import backend.mulkkam.cup.domain.vo.CupEmojiUrl;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@SQLRestriction("deleted_at IS NULL")
@SQLDelete(sql = "UPDATE cup_emoji SET deleted_at = NOW() WHERE id = ?")
@Entity
public class CupEmoji extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "url", nullable = false))
    private CupEmojiUrl url;

    public CupEmoji(String url) {
        this.url = new CupEmojiUrl(url);
    }

    public CupEmoji(CupEmojiUrl cupEmojiUrl) {
        this.url = cupEmojiUrl;
    }
}
