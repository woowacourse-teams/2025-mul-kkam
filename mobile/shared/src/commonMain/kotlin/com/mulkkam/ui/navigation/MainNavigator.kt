package com.mulkkam.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import com.mulkkam.domain.model.OnboardingInfo

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
    fun navigateToSplash() = navigateAndClearBackStack(AppRoute.Splash)

    fun navigateToLogin() = navigate(AppRoute.Login)

    // Onboarding
    fun navigateToOnboardingTerms() = navigateAndClearBackStack(OnboardingRoute.Terms)

    fun navigateToOnboardingNickname(onboardingInfo: OnboardingInfo? = null) =
        navigate(OnboardingRoute.Nickname(onboardingInfo))

    fun navigateToOnboardingBioInfo(onboardingInfo: OnboardingInfo) =
        navigate(OnboardingRoute.BioInfo(onboardingInfo))

    fun navigateToOnboardingTargetAmount(onboardingInfo: OnboardingInfo) =
        navigate(OnboardingRoute.TargetAmount(onboardingInfo))

    fun navigateToOnboardingCups(onboardingInfo: OnboardingInfo) =
        navigate(OnboardingRoute.Cups(onboardingInfo))

    // Home (Bottom Tab)
    fun navigateToHome() = navigateAndClearBackStack(HomeRoute.Home)

    fun navigateToEncyclopedia() = navigate(HomeRoute.Encyclopedia)

    fun navigateToHomeNotification() = navigate(HomeRoute.Notification)

    // History (Bottom Tab)
    fun navigateToHistory() = navigateAndClearBackStack(HistoryRoute.History)

    // Friends (Bottom Tab)
    fun navigateToFriends() = navigateAndClearBackStack(FriendsRoute.Friends)

    fun navigateToPendingFriends() = navigate(FriendsRoute.PendingFriends)

    fun navigateToSearchMembers() = navigate(FriendsRoute.SearchMembers)

    // Setting (Bottom Tab)
    fun navigateToSetting() = navigateAndClearBackStack(SettingRoute.Setting)

    fun navigateToAccountInfo() = navigate(SettingRoute.AccountInfo)

    fun navigateToSettingBioInfo() = navigate(SettingRoute.BioInfo)

    fun navigateToSettingCups() = navigate(SettingRoute.Cups)

    fun navigateToFeedback() = navigate(SettingRoute.Feedback)

    fun navigateToSettingNickname() = navigate(SettingRoute.Nickname)

    fun navigateToSettingNotification() = navigate(SettingRoute.Notification)

    fun navigateToReminder() = navigate(SettingRoute.Reminder)

    fun navigateToSettingTargetAmount() = navigate(SettingRoute.TargetAmount)

    fun navigateToSettingTerms() = navigate(SettingRoute.Terms)
}

@Composable
fun rememberMainNavigator(
    startDestination: Any = AppRoute.Splash,
): MainNavigator {
    val backStack = remember { listOf(startDestination).toMutableStateList() }
    return remember(backStack) {
        MainNavigator(backStack)
    }
}
