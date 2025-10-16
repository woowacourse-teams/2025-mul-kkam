package backend.mulkkam.friend.domain;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_FRIEND_RELATION;

import backend.mulkkam.common.exception.CommonException;

public enum FriendStatus {

    REQUESTED,
    ACCEPTED,
    ;

    public static void validRequest(FriendStatus friendStatus) {
        if (friendStatus == REQUESTED) {
            return;
        }
        throw new CommonException(INVALID_FRIEND_RELATION);
    }
}
