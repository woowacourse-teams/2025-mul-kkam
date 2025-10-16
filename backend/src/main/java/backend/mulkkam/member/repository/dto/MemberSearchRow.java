package backend.mulkkam.member.repository.dto;

import backend.mulkkam.friend.domain.FriendStatus;

public record MemberSearchRow(
        Long id,
        String nickname,
        FriendStatus friendStatus,
        boolean isRequesterMe
) {
}
