package com.mulkkam.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class UserAuthState {
    UNONBOARDED,
    ACTIVE_USER,
    ;

    companion object {
        fun from(hasCompletedOnboarding: Boolean): UserAuthState =
            when (hasCompletedOnboarding) {
                true -> ACTIVE_USER
                false -> UNONBOARDED
            }
    }
}
