package backend.mulkkam.cup.domain.vo;

import jakarta.persistence.Embeddable;

@Embeddable
public record CupEmojiUrl(String value) {

    public static final String DEFAULT_HISTORY_EMOJI_URL = "https://github.com/user-attachments/assets/df68b91b-772c-4feb-bc2a-59955fe74c57";

    public CupEmojiUrl {
        if (value == null || value.isBlank()) {
            value = DEFAULT_HISTORY_EMOJI_URL;
        }
    }

    public static CupEmojiUrl getDefault() {
        return new CupEmojiUrl(null);
    }
}
