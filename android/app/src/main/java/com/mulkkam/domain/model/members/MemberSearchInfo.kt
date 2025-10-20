package com.mulkkam.domain.model.members

data class MemberSearchInfo(
    val id: Long,
    val nickname: Nickname,
    val status: Status,
    val direction: Direction,
) {
    fun isFriends(): Boolean = status == Status.ACCEPTED

    fun isRequestedToMe(): Boolean = (status == Status.REQUESTED) && direction == Direction.REQUESTED_TO_ME

    fun isRequestedByMe(): Boolean = (status == Status.REQUESTED) && direction == Direction.REQUESTED_BY_ME
}
