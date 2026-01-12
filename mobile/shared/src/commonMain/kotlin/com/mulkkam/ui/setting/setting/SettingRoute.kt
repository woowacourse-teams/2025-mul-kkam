package com.mulkkam.ui.setting.setting

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import com.mulkkam.ui.setting.setting.model.SettingType

@Composable
fun SettingRoute(
    padding: PaddingValues,
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
        padding = padding,
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
