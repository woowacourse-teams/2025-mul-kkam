package backend.mulkkam.admin.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "어드민 섭취 기록 수정 요청")
public record UpdateAdminIntakeHistoryRequest(
        @Schema(description = "목표 섭취량(ml)", example = "2000")
        @NotNull(message = "목표 섭취량은 필수입니다.")
        @Positive(message = "목표 섭취량은 양수여야 합니다.")
        Integer targetAmount
) {
}
