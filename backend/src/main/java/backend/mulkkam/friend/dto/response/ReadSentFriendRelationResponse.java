package backend.mulkkam.friend.dto.response;

import backend.mulkkam.friend.repository.dto.SentFriendRelationSummary;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record ReadSentFriendRelationResponse(
        @Schema(description = "친구 신청 엔티티 id 및 닉네임")
        List<SentFriendRelationInfo> results,

        @Schema(description = "다음 요청 시 보내야할 id", example = "25")
        Long nextId,

        @Schema(description = "다음 데이터가 있는 지 체크", example = "true")
        boolean hasNext
) {

    public record SentFriendRelationInfo(
            @Schema(description = "친구 관계 id", example = "1")
            Long friendRequestId,

            @Schema(description = "친구의 멤버 id", example = "1")
            Long memberId,

            @Schema(description = "회원 닉네임", example = "밍곰")
            String memberNickname
    ) {
        public SentFriendRelationInfo(SentFriendRelationSummary sentFriendRelationSummary) {
            this(
                    sentFriendRelationSummary.friendRequestId(),
                    sentFriendRelationSummary.memberId(),
                    sentFriendRelationSummary.memberNickname()
            );
        }
    }
}
