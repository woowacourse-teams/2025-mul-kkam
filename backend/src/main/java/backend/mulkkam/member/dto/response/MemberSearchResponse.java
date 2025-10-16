package backend.mulkkam.member.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record MemberSearchResponse(
        @Schema(description = "검색 결과 목록 (id, 닉네임, 친구상태, 요청방향 포함)")
        List<MemberSearchItemResponse> memberSearchItemResponses,

        @Schema(description = "다음 요청 id")
        Long nextId,

        @Schema(description = "다음 값이 있는 지 boolean", example = "true")
        boolean hasNext
) {
    public static MemberSearchResponse empty() {
        return new MemberSearchResponse(List.of(), null, false);
    }
}
