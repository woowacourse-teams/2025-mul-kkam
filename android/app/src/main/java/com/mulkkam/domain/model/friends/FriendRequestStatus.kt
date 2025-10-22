package com.mulkkam.domain.model.friends

enum class FriendRequestStatus {
    ACCEPTED,
    REQUESTED,
    REJECTED,
    NONE,
    ;

    companion object {
        fun from(status: String): FriendRequestStatus =
            when (status) {
                ACCEPTED.name -> ACCEPTED
                REQUESTED.name -> REQUESTED
                REJECTED.name -> REJECTED
                else -> NONE
            }
    }
}
