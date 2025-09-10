package backend.mulkkam.cup.domain.vo;

import backend.mulkkam.cup.domain.IntakeType;
import jakarta.persistence.Embeddable;

@Embeddable
public record CupEmojiUrl(String value) {

    public static final String DEFAULT_WATER_EMOJI_URL = "https://github.com/user-attachments/assets/393fc8f9-bc46-4856-bfbe-889efc97151e";
    public static final String DEFAULT_COFFEE_EMOJI_URL = "https://github.com/user-attachments/assets/783767ab-ee37-4079-8e38-e08884a8de1c";

    public CupEmojiUrl {
        if (value == null || value.isBlank()) {
            value = DEFAULT_WATER_EMOJI_URL;
        }
    }

    public static CupEmojiUrl getDefaultByType(IntakeType type) {
        return switch (type) {
            case WATER -> new CupEmojiUrl(DEFAULT_WATER_EMOJI_URL);
            case COFFEE -> new CupEmojiUrl(DEFAULT_COFFEE_EMOJI_URL);
        };
    }
}
