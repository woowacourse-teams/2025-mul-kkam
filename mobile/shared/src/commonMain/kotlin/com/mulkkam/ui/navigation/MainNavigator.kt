package com.mulkkam.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList

@Stable
class MainNavigator internal constructor(
    val backStack: SnapshotStateList<Any>,
) {
    val currentRoute: Any?
        get() = backStack.lastOrNull()

    fun popBackStack(): Boolean =
        if (backStack.size > 1) {
            backStack.removeAt(backStack.lastIndex)
            true
        } else {
            false
        }

    fun navigate(route: Any) {
        backStack.add(route)
    }

    fun navigateAndClearBackStack(route: Any) {
        backStack.clear()
        backStack.add(route)
    }

    // App
    fun navigateToSplash() = navigateAndClearBackStack(_root_ide_package_.com.mulkkam.ui.navigation.AppRoute.Splash)

    fun navigateToLogin() = navigate(_root_ide_package_.com.mulkkam.ui.navigation.AppRoute.Login)

    // Onboarding
    fun navigateToOnboardingTerms() = navigateAndClearBackStack(_root_ide_package_.com.mulkkam.ui.navigation.OnboardingRoute.Terms)

    fun navigateToOnboardingNickname() = navigate(_root_ide_package_.com.mulkkam.ui.navigation.OnboardingRoute.Nickname)

    fun navigateToOnboardingBioInfo() = navigate(_root_ide_package_.com.mulkkam.ui.navigation.OnboardingRoute.BioInfo)

    fun navigateToOnboardingTargetAmount() = navigate(_root_ide_package_.com.mulkkam.ui.navigation.OnboardingRoute.TargetAmount)

    fun navigateToOnboardingCups() = navigate(_root_ide_package_.com.mulkkam.ui.navigation.OnboardingRoute.Cups)

    // Home (Bottom Tab)
    fun navigateToHome() = navigateAndClearBackStack(_root_ide_package_.com.mulkkam.ui.navigation.HomeRoute.Home)

    fun navigateToEncyclopedia() = navigate(_root_ide_package_.com.mulkkam.ui.navigation.HomeRoute.Encyclopedia)

    fun navigateToHomeNotification() = navigate(_root_ide_package_.com.mulkkam.ui.navigation.HomeRoute.Notification)

    // History (Bottom Tab)
    fun navigateToHistory() = navigateAndClearBackStack(_root_ide_package_.com.mulkkam.ui.navigation.HistoryRoute.History)

    // Friends (Bottom Tab)
    fun navigateToFriends() = navigateAndClearBackStack(_root_ide_package_.com.mulkkam.ui.navigation.FriendsRoute.Friends)

    fun navigateToPendingFriends() = navigate(_root_ide_package_.com.mulkkam.ui.navigation.FriendsRoute.PendingFriends)

    fun navigateToSearchMembers() = navigate(_root_ide_package_.com.mulkkam.ui.navigation.FriendsRoute.SearchMembers)

    // Setting (Bottom Tab)
    fun navigateToSetting() = navigateAndClearBackStack(_root_ide_package_.com.mulkkam.ui.navigation.SettingRoute.Setting)

    fun navigateToAccountInfo() = navigate(_root_ide_package_.com.mulkkam.ui.navigation.SettingRoute.AccountInfo)

    fun navigateToSettingBioInfo() = navigate(_root_ide_package_.com.mulkkam.ui.navigation.SettingRoute.BioInfo)

    fun navigateToSettingCups() = navigate(_root_ide_package_.com.mulkkam.ui.navigation.SettingRoute.Cups)

    fun navigateToFeedback() = navigate(_root_ide_package_.com.mulkkam.ui.navigation.SettingRoute.Feedback)

    fun navigateToSettingNickname() = navigate(_root_ide_package_.com.mulkkam.ui.navigation.SettingRoute.Nickname)

    fun navigateToSettingNotification() = navigate(_root_ide_package_.com.mulkkam.ui.navigation.SettingRoute.Notification)

    fun navigateToReminder() = navigate(_root_ide_package_.com.mulkkam.ui.navigation.SettingRoute.Reminder)

    fun navigateToSettingTargetAmount() = navigate(_root_ide_package_.com.mulkkam.ui.navigation.SettingRoute.TargetAmount)

    fun navigateToSettingTerms() = navigate(_root_ide_package_.com.mulkkam.ui.navigation.SettingRoute.Terms)
}

@Composable
fun rememberMainNavigator(
    startDestination: Any = _root_ide_package_.com.mulkkam.ui.navigation.AppRoute.Splash,
): com.mulkkam.ui.navigation.MainNavigator {
    val backStack = remember { listOf(startDestination).toMutableStateList() }
    return remember(backStack) {
        _root_ide_package_.com.mulkkam.ui.navigation
            .MainNavigator(backStack)
    }
}
