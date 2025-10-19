package backend.mulkkam.member.dto.response;

import static backend.mulkkam.member.dto.response.MemberSearchItemResponse.Direction.NONE;
import static backend.mulkkam.member.dto.response.MemberSearchItemResponse.Status.ACCEPTED;
import static backend.mulkkam.member.dto.response.MemberSearchItemResponse.Status.REQUESTED;

import backend.mulkkam.friend.domain.FriendRelationStatus;
import backend.mulkkam.member.repository.dto.MemberSearchRow;
import io.swagger.v3.oas.annotations.media.Schema;

public record MemberSearchItemResponse(
        @Schema(description = "회원 id", example = "1")
        Long id,

        @Schema(description = "회원 닉네임", example = "밍곰")
        String memberNickname,

        @Schema(description = "친구 관계 상태", example = "ACCEPTED")
        Status status,

        @Schema(description = "친구 관계 상태가 REQUESTED 일 때, 누가 보냈는 지", example = "REQUESTED_BY_ME")
        Direction direction
) {

    public enum Status {REQUESTED, ACCEPTED, NONE}

    public enum Direction {REQUESTED_BY_ME, REQUESTED_TO_ME, NONE}

    public static MemberSearchItemResponse of(MemberSearchRow memberSearchRow) {
        Status status = decideStatus(memberSearchRow);
        Direction direction = decideDirection(status, memberSearchRow);
        return new MemberSearchItemResponse(memberSearchRow.id(), memberSearchRow.nickname(), status, direction);
    }

    private static Status decideStatus(MemberSearchRow memberSearchRow) {
        if (memberSearchRow.friendRelationStatus() == FriendRelationStatus.ACCEPTED) {
            return ACCEPTED;
        }
        if (memberSearchRow.friendRelationStatus() == FriendRelationStatus.REQUESTED) {
            return REQUESTED;
        }
        return Status.NONE;
    }

    private static Direction decideDirection(
            Status status,
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
