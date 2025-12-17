package com.mulkkam.ui.navigation

import kotlinx.serialization.Serializable

sealed interface AppRoute {
    @Serializable
    data object Splash : com.mulkkam.ui.navigation.AppRoute

    @Serializable
    data object Login : com.mulkkam.ui.navigation.AppRoute
}

sealed interface OnboardingRoute {
    @Serializable
    data object Terms : com.mulkkam.ui.navigation.OnboardingRoute

    @Serializable
    data object Nickname : com.mulkkam.ui.navigation.OnboardingRoute

    @Serializable
    data object BioInfo : com.mulkkam.ui.navigation.OnboardingRoute

    @Serializable
    data object TargetAmount : com.mulkkam.ui.navigation.OnboardingRoute

    @Serializable
    data object Cups : com.mulkkam.ui.navigation.OnboardingRoute
}

sealed interface HomeRoute {
    @Serializable
    data object Home : com.mulkkam.ui.navigation.HomeRoute

    @Serializable
    data object Encyclopedia : com.mulkkam.ui.navigation.HomeRoute

    @Serializable
    data object Notification : com.mulkkam.ui.navigation.HomeRoute
}

sealed interface FriendsRoute {
    @Serializable
    data object Friends : com.mulkkam.ui.navigation.FriendsRoute

    @Serializable
    data object PendingFriends : com.mulkkam.ui.navigation.FriendsRoute

    @Serializable
    data object SearchMembers : com.mulkkam.ui.navigation.FriendsRoute
}

sealed interface HistoryRoute {
    @Serializable
    data object History : com.mulkkam.ui.navigation.HistoryRoute
}

sealed interface SettingRoute {
    @Serializable
    data object Setting : com.mulkkam.ui.navigation.SettingRoute

    @Serializable
    data object AccountInfo : com.mulkkam.ui.navigation.SettingRoute

    @Serializable
    data object BioInfo : com.mulkkam.ui.navigation.SettingRoute

    @Serializable
    data object Cups : com.mulkkam.ui.navigation.SettingRoute

    @Serializable
    data object Feedback : com.mulkkam.ui.navigation.SettingRoute

    @Serializable
    data object Nickname : com.mulkkam.ui.navigation.SettingRoute

    @Serializable
    data object Notification : com.mulkkam.ui.navigation.SettingRoute

    @Serializable
    data object Reminder : com.mulkkam.ui.navigation.SettingRoute

    @Serializable
    data object TargetAmount : com.mulkkam.ui.navigation.SettingRoute

    @Serializable
    data object Terms : com.mulkkam.ui.navigation.SettingRoute
}
