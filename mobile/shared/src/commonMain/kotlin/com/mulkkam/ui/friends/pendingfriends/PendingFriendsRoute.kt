package com.mulkkam.ui.friends.pendingfriends

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable

@Composable
fun PendingFriendsRoute(
    padding: PaddingValues,
    onNavigateToBack: () -> Boolean,
    snackbarHostState: SnackbarHostState,
) {
    PendingFriendsScreen(
        padding = padding,
        onNavigateToBack = onNavigateToBack,
        snackbarHostState = snackbarHostState,
    )
}
