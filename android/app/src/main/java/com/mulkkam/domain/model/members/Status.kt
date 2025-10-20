package com.mulkkam.domain.model.members

enum class Status {
    ACCEPTED,
    REQUESTED,
    REJECTED,
    NONE,
    ;

    companion object {
        fun from(status: String): Status =
            when (status) {
                ACCEPTED.name -> ACCEPTED
                REQUESTED.name -> REQUESTED
                REJECTED.name -> REJECTED
                else -> NONE
            }
    }
}
