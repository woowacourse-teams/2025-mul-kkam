package com.mulkkam.domain.model.members

enum class Direction {
    REQUESTED_BY_ME,
    REQUESTED_TO_ME,
    NONE,
    ;

    companion object {
        fun from(direction: String): Direction =
            when (direction) {
                REQUESTED_BY_ME.name -> REQUESTED_BY_ME
                REQUESTED_TO_ME.name -> REQUESTED_TO_ME
                else -> NONE
            }
    }
}
