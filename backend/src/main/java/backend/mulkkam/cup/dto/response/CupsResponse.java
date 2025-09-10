package backend.mulkkam.cup.dto.response;

import backend.mulkkam.cup.domain.Cup;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record CupsResponse(
        @Schema(
                description = "반환된 컵의 개수 (= cups 길이와 동일)",
                example = "1"
        )
        int size,
        @Schema(description = "사용자 컵 리스트")
        List<CupResponse> cups
) {
    public CupsResponse(List<Cup> cup) {
        this(
                cup.size(),
                cup.stream()
                        .map(CupResponse::new)
                        .toList()
        );
    }
}
