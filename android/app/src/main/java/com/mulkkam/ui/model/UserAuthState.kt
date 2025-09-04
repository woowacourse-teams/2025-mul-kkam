package com.mulkkam.ui.model

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
