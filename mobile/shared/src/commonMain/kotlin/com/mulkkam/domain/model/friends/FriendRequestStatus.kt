package com.mulkkam.domain.model.friends

import kotlinx.serialization.Serializable

@Serializable
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
