package com.mulkkam.ui.setting.reminder

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable

@Composable
fun ReminderRoute(
    padding: PaddingValues,
    onNavigateToBack: () -> Boolean,
    snackbarHostState: SnackbarHostState,
) {
    ReminderScreen(
        padding = padding,
        navigateToBack = onNavigateToBack,
        snackbarHostState = snackbarHostState,
    )
}
