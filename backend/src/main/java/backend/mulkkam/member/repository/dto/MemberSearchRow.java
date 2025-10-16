package backend.mulkkam.member.repository.dto;

import backend.mulkkam.friend.domain.FriendRelationStatus;

public record MemberSearchRow(
        Long id,
        String nickname,
        FriendRelationStatus friendRelationStatus,
        boolean isRequester
) {
}
