package com.mulkkam.ui.setting.setting

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable

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
        onNavigateToAccountInfo = onNavigateToAccountInfo,
        onNavigateToBioInfo = onNavigateToBioInfo,
        onNavigateToCups = onNavigateToCups,
        onNavigateToFeedback = onNavigateToFeedback,
        onNavigateToNickname = onNavigateToNickname,
        onNavigateToNotification = onNavigateToNotification,
        onNavigateToReminder = onNavigateToReminder,
        onNavigateToTargetAmount = onNavigateToTargetAmount,
        onNavigateToTerms = onNavigateToTerms,
    )
}
