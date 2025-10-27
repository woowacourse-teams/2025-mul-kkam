package backend.mulkkam.friend.repository.dto;

public record SentFriendRelationSummary(
        Long friendRequestId,
        Long memberId,
        String memberNickname
) {
}
