package com.mulkkam.ui.setting

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import com.mulkkam.ui.navigation.MainNavigator
import com.mulkkam.ui.navigation.NavEntry
import com.mulkkam.ui.navigation.SettingRoute
import com.mulkkam.ui.navigation.entry
import com.mulkkam.ui.setting.accountinfo.AccountInfoScreen
import com.mulkkam.ui.setting.bioinfo.BioInfoScreen
import com.mulkkam.ui.setting.cups.CupsScreen
import com.mulkkam.ui.setting.feedback.FeedbackScreen
import com.mulkkam.ui.setting.nickname.NicknameScreen
import com.mulkkam.ui.setting.notification.NotificationScreen
import com.mulkkam.ui.setting.reminder.ReminderScreen
import com.mulkkam.ui.setting.setting.SettingScreen
import com.mulkkam.ui.setting.targetamount.TargetAmountScreen
import com.mulkkam.ui.setting.terms.TermsScreen

object SettingNavGraph {
    @Composable
    fun entryProvider(
        route: SettingRoute,
        padding: PaddingValues,
        navigator: MainNavigator,
    ): NavEntry<SettingRoute> =
        when (route) {
            is SettingRoute.Setting -> {
                entry(route) {
                    SettingScreen(
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
                    )
                }
            }

            is SettingRoute.AccountInfo -> {
                entry(route) {
                    AccountInfoScreen(
                        padding = padding,
                        onNavigateBack = navigator::popBackStack,
                    )
                }
            }

            is SettingRoute.BioInfo -> {
                entry(route) {
                    BioInfoScreen(
                        padding = padding,
                        onNavigateBack = navigator::popBackStack,
                    )
                }
            }

            is SettingRoute.Cups -> {
                entry(route) {
                    CupsScreen(
                        padding = padding,
                        onNavigateBack = navigator::popBackStack,
                    )
                }
            }

            is SettingRoute.Feedback -> {
                entry(route) {
                    FeedbackScreen(
                        padding = padding,
                        onNavigateBack = navigator::popBackStack,
                    )
                }
            }

            is SettingRoute.Nickname -> {
                entry(route) {
                    NicknameScreen(
                        padding = padding,
                        onNavigateBack = navigator::popBackStack,
                    )
                }
            }

            is SettingRoute.Notification -> {
                entry(route) {
                    NotificationScreen(
                        padding = padding,
                        onNavigateBack = navigator::popBackStack,
                    )
                }
            }

            is SettingRoute.Reminder -> {
                entry(route) {
                    ReminderScreen(
                        padding = padding,
                        onNavigateBack = navigator::popBackStack,
                    )
                }
            }

            is SettingRoute.TargetAmount -> {
                entry(route) {
                    TargetAmountScreen(
                        padding = padding,
                        onNavigateBack = navigator::popBackStack,
                    )
                }
            }

            is SettingRoute.Terms -> {
                entry(route) {
                    TermsScreen(
                        padding = padding,
                        onNavigateBack = navigator::popBackStack,
                    )
                }
            }
        }
}
