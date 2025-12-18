package com.mulkkam.ui.setting.notification

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable

@Composable
fun NotificationRoute(
    padding: PaddingValues,
    onNavigateToBack: () -> Boolean,
) {
    NotificationScreen(
        padding = padding,
        onNavigateToBack = onNavigateToBack,
    )
}
