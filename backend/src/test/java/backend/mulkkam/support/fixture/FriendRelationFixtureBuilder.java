package backend.mulkkam.support.fixture;

import backend.mulkkam.friend.domain.FriendRelation;
import backend.mulkkam.friend.domain.FriendStatus;

public class FriendRelationFixtureBuilder {

    private Long requesterId = 1L;
    private Long addresseeId = 2L;
    private FriendStatus friendStatus = FriendStatus.REQUESTED;

    private FriendRelationFixtureBuilder() {
    }

    public static FriendRelationFixtureBuilder builder() {
        return new FriendRelationFixtureBuilder();
    }

    public FriendRelationFixtureBuilder requesterId(Long requesterId) {
        this.requesterId = requesterId;
        return this;
    }

    public FriendRelationFixtureBuilder addresseeId(Long addresseeId) {
        this.addresseeId = addresseeId;
        return this;
    }

    public FriendRelationFixtureBuilder friendStatus(FriendStatus friendStatus) {
        this.friendStatus = friendStatus;
        return this;
    }

    public FriendRelation build() {
        return new FriendRelation(
                requesterId,
                addresseeId,
                friendStatus
        );
    }
}
