package backend.mulkkam.friend.domain;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_FRIEND_RELATION;

import backend.mulkkam.common.exception.CommonException;

public enum FriendRelationStatus {

    REQUESTED,
    ACCEPTED,
    ;

    public static void validRequest(FriendRelationStatus friendStatus) {
        if (friendStatus == REQUESTED) {
            return;
        }
        throw new CommonException(INVALID_FRIEND_RELATION);
    }
}
