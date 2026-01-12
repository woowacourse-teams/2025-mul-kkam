package com.mulkkam.ui.setting.accountinfo

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable

@Composable
fun AccountInfoRoute(
    padding: PaddingValues,
    onNavigateToBack: () -> Boolean,
    snackbarHostState: SnackbarHostState,
) {
    AccountInfoScreen(
        padding = padding,
        onNavigateToBack = onNavigateToBack,
        snackbarHostState = snackbarHostState,
    )
}
