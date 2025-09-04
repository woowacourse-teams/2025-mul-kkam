package backend.mulkkam.cup.dto.request;

import backend.mulkkam.cup.domain.IntakeType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "컵 생성 요청")
public record CreateCupRequest(
        @Schema(description = "컵 이름", example = "스타벅스 텀블러")
        String cupNickname,

        @Schema(description = "컵 용량", example = "500")
        Integer cupAmount,

        @Schema(description = "컵 랭크", example = "1")
        int cupRank, // TODO 2025. 9. 4. 21:03: Integer vs int

        @Schema(description = "음료 종류", implementation = IntakeType.class)
        String intakeType,

        @Schema(description = "컵 이모지 id", example = "1")
        Long cupEmojiId
) {
}
