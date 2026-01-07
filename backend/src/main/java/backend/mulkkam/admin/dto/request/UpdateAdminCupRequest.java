package backend.mulkkam.admin.dto.request;

import backend.mulkkam.cup.domain.IntakeType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "어드민 컵 수정 요청")
public record UpdateAdminCupRequest(
        @Schema(description = "컵 닉네임", example = "내 텀블러")
        @NotBlank(message = "컵 닉네임은 필수입니다.")
        String nickname,

        @Schema(description = "컵 용량(ml)", example = "500")
        @NotNull(message = "컵 용량은 필수입니다.")
        @Positive(message = "컵 용량은 양수여야 합니다.")
        Integer cupAmount,

        @Schema(description = "음료 종류", example = "WATER")
        @NotNull(message = "음료 종류는 필수입니다.")
        IntakeType intakeType,

        @Schema(description = "컵 이모지 ID", example = "1")
        @NotNull(message = "컵 이모지 ID는 필수입니다.")
        Long cupEmojiId
) {
}
