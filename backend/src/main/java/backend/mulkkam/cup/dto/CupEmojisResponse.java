package backend.mulkkam.cup.dto;

import java.util.List;

public record CupEmojisResponse(
        List<CupEmojiResponse> cups
) {
}
