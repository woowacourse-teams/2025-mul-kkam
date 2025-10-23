package com.mulkkam.domain.model.members

import com.mulkkam.domain.model.friends.FriendRequestStatus

data class MemberSearchInfo(
    val id: Long,
    val nickname: Nickname,
    val status: FriendRequestStatus,
    val direction: RequestDirection,
) {
    fun isFriends(): Boolean = status == FriendRequestStatus.ACCEPTED

    fun isRequestedToMe(): Boolean = (status == FriendRequestStatus.REQUESTED) && direction == RequestDirection.REQUESTED_TO_ME

    fun isRequestedByMe(): Boolean = (status == FriendRequestStatus.REQUESTED) && direction == RequestDirection.REQUESTED_BY_ME
}
