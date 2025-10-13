package backend.mulkkam.member.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record MemberSearchResponse(
        @Schema(description = "id 및 닉네임")
        List<MemberIdNicknameResponse> memberIdNicknameResponses,
        
        @Schema(description = "다음 값이 있는 지 boolean", example = "true")
        boolean hasNext
) {
}
