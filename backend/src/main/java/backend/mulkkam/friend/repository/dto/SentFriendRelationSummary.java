package backend.mulkkam.friend.repository.dto;

public record SentFriendRelationSummary(
        Long friendRequestId,
        String memberNickname
) {
}
