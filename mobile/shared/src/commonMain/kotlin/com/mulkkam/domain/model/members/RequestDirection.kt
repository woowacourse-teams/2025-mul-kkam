package com.mulkkam.domain.model.members

import kotlinx.serialization.Serializable

@Serializable
enum class RequestDirection {
    REQUESTED_BY_ME,
    REQUESTED_TO_ME,
    NONE,
    ;

    companion object {
        fun from(direction: String): RequestDirection =
            when (direction) {
                REQUESTED_BY_ME.name -> REQUESTED_BY_ME
                REQUESTED_TO_ME.name -> REQUESTED_TO_ME
                else -> NONE
            }
    }
}
