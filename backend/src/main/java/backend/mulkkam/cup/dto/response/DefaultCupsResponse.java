package backend.mulkkam.cup.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record DefaultCupsResponse(
        @Schema(description = "컵 리스트")
        List<DefaultCupResponse> cups
) {
}
