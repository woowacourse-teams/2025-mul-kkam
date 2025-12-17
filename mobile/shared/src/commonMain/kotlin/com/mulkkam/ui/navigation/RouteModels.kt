package com.mulkkam.ui.navigation

import kotlinx.serialization.Serializable

sealed interface AppRoute {
    @Serializable
    data object Splash : AppRoute

    @Serializable
    data object Login : AppRoute
}

sealed interface OnboardingRoute {
    @Serializable
    data object Terms : OnboardingRoute

    @Serializable
    data object Nickname : OnboardingRoute

    @Serializable
    data object BioInfo : OnboardingRoute

    @Serializable
    data object TargetAmount : OnboardingRoute

    @Serializable
    data object Cups : OnboardingRoute
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
