package backend.mulkkam.cup.dto;

import backend.mulkkam.cup.domain.CupEmoji;
import io.swagger.v3.oas.annotations.media.Schema;

public record CupEmojiResponse(
        @Schema(description = "컵 이모지 식별자", example = "1")
        Long id,
        @Schema(description = "컵 이모지 url", example = "http://example.com")
        String cupEmojiUrl
) {

    public CupEmojiResponse(CupEmoji cupEmoji) {
        this(cupEmoji.getId(), cupEmoji.getUrl().value());
    }
}
