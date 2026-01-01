package com.mulkkam.ui.friends.friends

import androidx.compose.runtime.Composable

@Composable
fun FriendsRoute(
    onNavigateToPendingFriends: () -> Unit,
    onNavigateToSearchMembers: () -> Unit,
) {
    FriendsScreen(
        navigateToSearch = onNavigateToSearchMembers,
        navigateToFriendRequests = onNavigateToPendingFriends,
    )
}
