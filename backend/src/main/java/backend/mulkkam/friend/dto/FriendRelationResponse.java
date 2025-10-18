package backend.mulkkam.friend.dto;

import backend.mulkkam.friend.repository.dto.MemberInfoOfFriendRelation;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record FriendRelationResponse(
        @Schema(description = "친구인 멤버의 정보")
        List<MemberInfo> informationOfMembers,

        @Schema(description = "다음 요청의 id")
        Long nextId,

        @Schema(description = "다음 값 존재 여부", example = "true")
        boolean hasNext
) {
    public record MemberInfo(
            @Schema(description = "멤버의 id", example = "1")
            long memberId,

            @Schema(description = "멤버의 닉네임", example = "돈먹환")
            String memberNickname
    ) {
        public MemberInfo(MemberInfoOfFriendRelation memberInfoOfFriendRelation) {
            this(
                    memberInfoOfFriendRelation.memberId(),
                    memberInfoOfFriendRelation.nickname()
            );
        }
    }
}
