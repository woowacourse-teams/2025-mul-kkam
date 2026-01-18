package com.mulkkam.ui.pendingfriends

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable

@Composable
fun PendingFriendsRoute(
    padding: PaddingValues,
    onNavigateToBack: () -> Boolean,
    snackbarHostState: SnackbarHostState,
    onFriendAccepted: () -> Unit = {},
) {
    PendingFriendsScreen(
        padding = padding,
        onNavigateToBack = onNavigateToBack,
        snackbarHostState = snackbarHostState,
        onFriendAccepted = onFriendAccepted,
    )
}
