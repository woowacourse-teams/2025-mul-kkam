package backend.mulkkam.support.fixture;

import backend.mulkkam.friend.domain.FriendRelation;
import backend.mulkkam.friend.domain.FriendRelationStatus;

public class FriendRelationFixtureBuilder {

    private Long requesterId = 1L;
    private Long addresseeId = 2L;
    private FriendRelationStatus friendRelationStatus = FriendRelationStatus.REQUESTED;

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

    public FriendRelationFixtureBuilder friendStatus(FriendRelationStatus friendRelationStatus) {
        this.friendRelationStatus = friendRelationStatus;
        return this;
    }

    public FriendRelation build() {
        return new FriendRelation(
                requesterId,
                addresseeId,
                friendRelationStatus
        );
    }
}

