package com.mulkkam.ui.home.notification

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable

@Composable
fun NotificationRoute(
    padding: PaddingValues,
    onNavigateToBack: () -> Unit,
    snackbarHostState: SnackbarHostState,
) {
    NotificationScreen(
        padding = padding,
        onNavigateToBack = onNavigateToBack,
        snackbarHostState = snackbarHostState,
    )
}
