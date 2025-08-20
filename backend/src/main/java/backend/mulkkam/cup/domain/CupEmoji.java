package backend.mulkkam.cup.domain;

import backend.mulkkam.common.domain.BaseEntity;
import jakarta.persistence.Column;
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

    private static final String DEFAULT_CUP_EMOJI_URL = "https://github.com/user-attachments/assets/783767ab-ee37-4079-8e38-e08884a8de1c";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "url", nullable = false)
    private String url;

    public CupEmoji(String url) {
        this.url = url;
    }

    public static String getDefaultCupEmojiUrl() {
        return DEFAULT_CUP_EMOJI_URL;
    }
}
