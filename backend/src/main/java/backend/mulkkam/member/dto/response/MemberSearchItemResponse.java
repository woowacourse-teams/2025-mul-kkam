package backend.mulkkam.member.dto.response;

import static backend.mulkkam.friend.dto.FriendRequestStatus.ACCEPTED;
import static backend.mulkkam.friend.dto.FriendRequestStatus.REQUESTED;
import static backend.mulkkam.member.dto.response.MemberSearchItemResponse.Direction.NONE;

import backend.mulkkam.friend.domain.FriendRelationStatus;
import backend.mulkkam.friend.dto.FriendRequestStatus;
import backend.mulkkam.member.repository.dto.MemberSearchRow;
import io.swagger.v3.oas.annotations.media.Schema;

public record MemberSearchItemResponse(
        @Schema(description = "회원 id", example = "1")
        Long id,

        @Schema(description = "회원 닉네임", example = "밍곰")
        String memberNickname,

        @Schema(description = "친구 관계 상태", example = "ACCEPTED")
        FriendRequestStatus status,

        @Schema(description = "친구 관계 상태가 REQUESTED 일 때, 누가 보냈는 지", example = "REQUESTED_BY_ME")
        Direction direction
) {

    public enum Direction {REQUESTED_BY_ME, REQUESTED_TO_ME, NONE}

    public static MemberSearchItemResponse of(MemberSearchRow memberSearchRow) {
        FriendRequestStatus status = decideStatus(memberSearchRow);
        Direction direction = decideDirection(status, memberSearchRow);
        return new MemberSearchItemResponse(memberSearchRow.id(), memberSearchRow.nickname(), status, direction);
    }

    private static FriendRequestStatus decideStatus(MemberSearchRow memberSearchRow) {
        if (memberSearchRow.friendRelationStatus() == FriendRelationStatus.ACCEPTED) {
            return ACCEPTED;
        }
        if (memberSearchRow.friendRelationStatus() == FriendRelationStatus.REQUESTED) {
            return REQUESTED;
        }
        return FriendRequestStatus.NONE;
    }

    private static Direction decideDirection(
            FriendRequestStatus status,
            MemberSearchRow memberSearchRow
    ) {
        if (status == REQUESTED) {
            if (memberSearchRow.isRequester()) {
                return Direction.REQUESTED_BY_ME;
            }
            return Direction.REQUESTED_TO_ME;
        }
        return NONE;
    }
}
