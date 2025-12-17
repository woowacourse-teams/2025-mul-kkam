package com.mulkkam.ui.setting

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import com.mulkkam.ui.navigation.MainNavigator
import com.mulkkam.ui.navigation.SettingRoute

object SettingNavGraph {
    @Composable
    fun entryProvider(
        route: com.mulkkam.ui.navigation.SettingRoute,
        padding: PaddingValues,
        navigator: com.mulkkam.ui.navigation.MainNavigator,
    ): com.mulkkam.ui.core.NavEntry<com.mulkkam.ui.navigation.SettingRoute> =
        when (route) {
            is com.mulkkam.ui.navigation.SettingRoute.Setting -> {
                _root_ide_package_.com.mulkkam.ui.core.entry(route) {
                    _root_ide_package_.com.mulkkam.ui.setting.setting.SettingScreen(
                        padding = padding,
                        onNavigateToAccountInfo = com.mulkkam.ui.navigation.MainNavigator::navigateToAccountInfo,
                        onNavigateToBioInfo = com.mulkkam.ui.navigation.MainNavigator::navigateToSettingBioInfo,
                        onNavigateToCups = com.mulkkam.ui.navigation.MainNavigator::navigateToSettingCups,
                        onNavigateToFeedback = com.mulkkam.ui.navigation.MainNavigator::navigateToFeedback,
                        onNavigateToNickname = com.mulkkam.ui.navigation.MainNavigator::navigateToSettingNickname,
                        onNavigateToNotification = com.mulkkam.ui.navigation.MainNavigator::navigateToSettingNotification,
                        onNavigateToReminder = com.mulkkam.ui.navigation.MainNavigator::navigateToReminder,
                        onNavigateToTargetAmount = com.mulkkam.ui.navigation.MainNavigator::navigateToSettingTargetAmount,
                        onNavigateToTerms = com.mulkkam.ui.navigation.MainNavigator::navigateToSettingTerms,
                    )
                }
            }

            is com.mulkkam.ui.navigation.SettingRoute.AccountInfo -> {
                _root_ide_package_.com.mulkkam.ui.core.entry(route) {
                    _root_ide_package_.com.mulkkam.ui.setting.accountinfo.AccountInfoScreen(
                        padding = padding,
                        onNavigateBack = com.mulkkam.ui.navigation.MainNavigator::popBackStack,
                    )
                }
            }

            is com.mulkkam.ui.navigation.SettingRoute.BioInfo -> {
                _root_ide_package_.com.mulkkam.ui.core.entry(route) {
                    _root_ide_package_.com.mulkkam.ui.setting.bioinfo.BioInfoScreen(
                        padding = padding,
                        onNavigateBack = com.mulkkam.ui.navigation.MainNavigator::popBackStack,
                    )
                }
            }

            is com.mulkkam.ui.navigation.SettingRoute.Cups -> {
                _root_ide_package_.com.mulkkam.ui.core.entry(route) {
                    _root_ide_package_.com.mulkkam.ui.setting.cups.CupsScreen(
                        padding = padding,
                        onNavigateBack = com.mulkkam.ui.navigation.MainNavigator::popBackStack,
                    )
                }
            }

            is com.mulkkam.ui.navigation.SettingRoute.Feedback -> {
                _root_ide_package_.com.mulkkam.ui.core.entry(route) {
                    _root_ide_package_.com.mulkkam.ui.setting.feedback.FeedbackScreen(
                        padding = padding,
                        onNavigateBack = com.mulkkam.ui.navigation.MainNavigator::popBackStack,
                    )
                }
            }

            is com.mulkkam.ui.navigation.SettingRoute.Nickname -> {
                _root_ide_package_.com.mulkkam.ui.core.entry(route) {
                    _root_ide_package_.com.mulkkam.ui.setting.nickname.NicknameScreen(
                        padding = padding,
                        onNavigateBack = com.mulkkam.ui.navigation.MainNavigator::popBackStack,
                    )
                }
            }

            is com.mulkkam.ui.navigation.SettingRoute.Notification -> {
                _root_ide_package_.com.mulkkam.ui.core.entry(route) {
                    _root_ide_package_.com.mulkkam.ui.setting.notification.NotificationScreen(
                        padding = padding,
                        onNavigateBack = com.mulkkam.ui.navigation.MainNavigator::popBackStack,
                    )
                }
            }

            is com.mulkkam.ui.navigation.SettingRoute.Reminder -> {
                _root_ide_package_.com.mulkkam.ui.core.entry(route) {
                    _root_ide_package_.com.mulkkam.ui.setting.reminder.ReminderScreen(
                        padding = padding,
                        onNavigateBack = com.mulkkam.ui.navigation.MainNavigator::popBackStack,
                    )
                }
            }

            is com.mulkkam.ui.navigation.SettingRoute.TargetAmount -> {
                _root_ide_package_.com.mulkkam.ui.core.entry(route) {
                    _root_ide_package_.com.mulkkam.ui.setting.targetamount.TargetAmountScreen(
                        padding = padding,
                        onNavigateBack = com.mulkkam.ui.navigation.MainNavigator::popBackStack,
                    )
                }
            }

            is com.mulkkam.ui.navigation.SettingRoute.Terms -> {
                _root_ide_package_.com.mulkkam.ui.core.entry(route) {
                    _root_ide_package_.com.mulkkam.ui.setting.terms.TermsScreen(
                        padding = padding,
                        onNavigateBack = com.mulkkam.ui.navigation.MainNavigator::popBackStack,
                    )
                }
            }
        }
}
