package com.mulkkam.ui.setting.setting

import androidx.compose.runtime.Composable
import com.mulkkam.ui.setting.setting.model.SettingType

@Composable
fun SettingRoute(
    onNavigateToAccountInfo: () -> Unit,
    onNavigateToBioInfo: () -> Unit,
    onNavigateToCups: () -> Unit,
    onNavigateToFeedback: () -> Unit,
    onNavigateToNickname: () -> Unit,
    onNavigateToNotification: () -> Unit,
    onNavigateToReminder: () -> Unit,
    onNavigateToTargetAmount: () -> Unit,
    onNavigateToTerms: () -> Unit,
) {
    SettingScreen(
        navigateToSettingType = { type ->
            when (type) {
                SettingType.NICKNAME -> onNavigateToNickname()
                SettingType.BODY_INFO -> onNavigateToBioInfo()
                SettingType.ACCOUNT_INFO -> onNavigateToAccountInfo()
                SettingType.MY_CUP -> onNavigateToCups()
                SettingType.GOAL -> onNavigateToTargetAmount()
                SettingType.PUSH_NOTIFICATION -> onNavigateToNotification()
                SettingType.FEEDBACK -> onNavigateToFeedback()
                SettingType.TERMS -> onNavigateToTerms()
                SettingType.REMINDER -> onNavigateToReminder()
            }
        },
    )
}
