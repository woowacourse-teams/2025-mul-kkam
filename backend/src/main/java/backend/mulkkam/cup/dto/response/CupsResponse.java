package backend.mulkkam.cup.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record CupsResponse(
        @Schema(description = "사용자 컵 리스트")
        List<CupResponse> cups
) {
}
