package com.mulkkam.ui.setting.reminder

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable

@Composable
fun ReminderRoute(
    padding: PaddingValues,
    onNavigateToBack: () -> Boolean,
) {
    ReminderScreen(
        padding = padding,
        onNavigateToBack = onNavigateToBack,
    )
}
