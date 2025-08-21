package backend.mulkkam.cup.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "컵 이모지 리스트 응답")
public record CupEmojisResponse(

        @Schema(description = "컵 이모지 목록")
        List<CupEmojiResponse> cups
) {
}
