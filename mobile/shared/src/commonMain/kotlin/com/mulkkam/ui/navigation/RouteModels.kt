package com.mulkkam.ui.navigation

import com.mulkkam.domain.model.OnboardingInfo
import kotlinx.serialization.Serializable

sealed interface AuthRoute {
    @Serializable
    data object Splash : AuthRoute

    @Serializable
    data object Login : AuthRoute
}

sealed interface OnboardingRoute {
    @Serializable
    data object Terms : OnboardingRoute

    @Serializable
    data class Nickname(
        val onboardingInfo: OnboardingInfo? = null,
    ) : OnboardingRoute

    @Serializable
    data class BioInfo(
        val onboardingInfo: OnboardingInfo,
    ) : OnboardingRoute

    @Serializable
    data class TargetAmount(
        val onboardingInfo: OnboardingInfo,
    ) : OnboardingRoute

    @Serializable
    data class Cups(
        val onboardingInfo: OnboardingInfo,
    ) : OnboardingRoute
}

sealed interface HomeRoute {
    @Serializable
    data object Home : HomeRoute

    @Serializable
    data object Encyclopedia : HomeRoute

    @Serializable
    data object Notification : HomeRoute
}

sealed interface FriendsRoute {
    @Serializable
    data object Friends : FriendsRoute

    @Serializable
    data object PendingFriends : FriendsRoute

    @Serializable
    data object SearchMembers : FriendsRoute
}

sealed interface HistoryRoute {
    @Serializable
    data object History : HistoryRoute
}

sealed interface SettingRoute {
    @Serializable
    data object Setting : SettingRoute

    @Serializable
    data object AccountInfo : SettingRoute

    @Serializable
    data object BioInfo : SettingRoute

    @Serializable
    data object Cups : SettingRoute

    @Serializable
    data object Feedback : SettingRoute

    @Serializable
    data object Nickname : SettingRoute

    @Serializable
    data object Notification : SettingRoute

    @Serializable
    data object Reminder : SettingRoute

    @Serializable
    data object TargetAmount : SettingRoute

    @Serializable
    data object Terms : SettingRoute
}
