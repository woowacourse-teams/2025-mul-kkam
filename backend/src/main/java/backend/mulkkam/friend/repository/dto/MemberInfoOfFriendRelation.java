package backend.mulkkam.friend.repository.dto;

public record MemberInfoOfFriendRelation(
        Long friendRelationId,
        Long memberId,
        String nickname
) {
}
