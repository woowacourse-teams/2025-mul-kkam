package backend.mulkkam.support.fixture;

import backend.mulkkam.friend.domain.FriendRequest;

public class FriendRequestFixtureBuilder {

    private Long requesterId = 1L;
    private Long addresseeId = 2L;

    private FriendRequestFixtureBuilder(FriendRequest friendRequest) {

    }

    public FriendRequestFixtureBuilder requesterId(Long requesterId) {
        this.requesterId = requesterId;
        return this;
    }

    public FriendRequestFixtureBuilder addresseeId(Long addresseeId) {
        this.addresseeId = addresseeId;
        return this;
    }

    public FriendRequest build() {
        return new FriendRequest(
                requesterId,
                addresseeId
        );
    }
}
