package com.mulkkam.ui.friends.friends

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable

@Composable
fun FriendsRoute(
    padding: PaddingValues,
    onNavigateToPendingFriends: () -> Unit,
    onNavigateToSearchMembers: () -> Unit,
    snackbarHostState: SnackbarHostState,
) {
    FriendsScreen(
        padding = padding,
        onNavigateToPendingFriends = onNavigateToPendingFriends,
        onNavigateToSearchMembers = onNavigateToSearchMembers,
        snackbarHostState = snackbarHostState,
    )
}
