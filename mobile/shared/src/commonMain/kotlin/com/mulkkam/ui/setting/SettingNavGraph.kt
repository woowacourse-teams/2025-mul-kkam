package com.mulkkam.ui.setting

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import com.mulkkam.ui.navigation.MainNavigator
import com.mulkkam.ui.navigation.NavEntry
import com.mulkkam.ui.navigation.SettingRoute
import com.mulkkam.ui.navigation.entry
import com.mulkkam.ui.setting.accountinfo.AccountInfoRoute
import com.mulkkam.ui.setting.bioinfo.BioInfoRoute
import com.mulkkam.ui.setting.cups.CupsRoute
import com.mulkkam.ui.setting.feedback.FeedbackRoute
import com.mulkkam.ui.setting.nickname.NicknameRoute
import com.mulkkam.ui.setting.notification.NotificationRoute
import com.mulkkam.ui.setting.reminder.ReminderRoute
import com.mulkkam.ui.setting.setting.SettingRoute
import com.mulkkam.ui.setting.targetamount.TargetAmountRoute
import com.mulkkam.ui.setting.terms.TermsRoute

object SettingNavGraph {
    @Composable
    fun entryProvider(
        route: SettingRoute,
        padding: PaddingValues,
        navigator: MainNavigator,
        snackbarHostState: SnackbarHostState,
    ): NavEntry<SettingRoute> =
        when (route) {
            is SettingRoute.Setting -> {
                entry(route) {
                    SettingRoute(
                        padding = padding,
                        onNavigateToAccountInfo = navigator::navigateToAccountInfo,
                        onNavigateToBioInfo = navigator::navigateToSettingBioInfo,
                        onNavigateToCups = navigator::navigateToSettingCups,
                        onNavigateToFeedback = navigator::navigateToFeedback,
                        onNavigateToNickname = navigator::navigateToSettingNickname,
                        onNavigateToNotification = navigator::navigateToSettingNotification,
                        onNavigateToReminder = navigator::navigateToReminder,
                        onNavigateToTargetAmount = navigator::navigateToSettingTargetAmount,
                        onNavigateToTerms = navigator::navigateToSettingTerms,
                        snackbarHostState = snackbarHostState,
                    )
                }
            }

            is SettingRoute.AccountInfo -> {
                entry(route) {
                    AccountInfoRoute(
                        padding = padding,
                        onNavigateToBack = navigator::popBackStack,
                        snackbarHostState = snackbarHostState,
                    )
                }
            }

            is SettingRoute.BioInfo -> {
                entry(route) {
                    BioInfoRoute(
                        padding = padding,
                        onNavigateToBack = navigator::popBackStack,
                        snackbarHostState = snackbarHostState,
                    )
                }
            }

            is SettingRoute.Cups -> {
                entry(route) {
                    CupsRoute(
                        padding = padding,
                        onNavigateToBack = navigator::popBackStack,
                        snackbarHostState = snackbarHostState,
                    )
                }
            }

            is SettingRoute.Feedback -> {
                entry(route) {
                    FeedbackRoute(
                        padding = padding,
                        onNavigateToBack = navigator::popBackStack,
                    )
                }
            }

            is SettingRoute.Nickname -> {
                entry(route) {
                    NicknameRoute(
                        padding = padding,
                        onNavigateToBack = navigator::popBackStack,
                        snackbarHostState = snackbarHostState,
                    )
                }
            }

            is SettingRoute.Notification -> {
                entry(route) {
                    NotificationRoute(
                        padding = padding,
                        onNavigateToBack = navigator::popBackStack,
                        snackbarHostState = snackbarHostState,
                    )
                }
            }

            is SettingRoute.Reminder -> {
                entry(route) {
                    ReminderRoute(
                        padding = padding,
                        onNavigateToBack = navigator::popBackStack,
                        snackbarHostState = snackbarHostState,
                    )
                }
            }

            is SettingRoute.TargetAmount -> {
                entry(route) {
                    TargetAmountRoute(
                        padding = padding,
                        onNavigateToBack = navigator::popBackStack,
                        snackbarHostState = snackbarHostState,
                    )
                }
            }

            is SettingRoute.Terms -> {
                entry(route) {
                    TermsRoute(
                        padding = padding,
                        onNavigateToBack = navigator::popBackStack,
                    )
                }
            }
        }
}
