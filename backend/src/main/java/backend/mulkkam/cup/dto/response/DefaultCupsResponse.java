package backend.mulkkam.cup.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record DefaultCupsResponse(
        @Schema(
                description = "반환된 컵의 개수 (= cups 길이와 동일)",
                example = "1"
        )
        int size,
        @Schema(description = "컵 리스트")
        List<DefaultCupResponse> cups
) {
}
