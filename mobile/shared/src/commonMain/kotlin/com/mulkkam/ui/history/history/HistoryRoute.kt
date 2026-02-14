package com.mulkkam.ui.history.history

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable

@Composable
fun HistoryRoute(
    padding: PaddingValues,
    snackbarHostState: SnackbarHostState,
) {
    HistoryScreen(padding = padding, snackbarHostState = snackbarHostState)
}
