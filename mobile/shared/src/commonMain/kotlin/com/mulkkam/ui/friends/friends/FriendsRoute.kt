package com.mulkkam.ui.friends.friends

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable

@Composable
fun FriendsRoute(
    padding: PaddingValues,
    onNavigateToPendingFriends: () -> Unit,
    onNavigateToSearchMembers: () -> Unit,
) {
    FriendsScreen(
        padding = padding,
        onNavigateToPendingFriends = onNavigateToPendingFriends,
        onNavigateToSearchMembers = onNavigateToSearchMembers,
    )
}
