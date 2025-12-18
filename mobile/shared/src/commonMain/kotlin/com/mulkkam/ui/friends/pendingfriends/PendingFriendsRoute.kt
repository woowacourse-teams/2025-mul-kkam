package com.mulkkam.ui.friends.pendingfriends

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable

@Composable
fun PendingFriendsRoute(
    padding: PaddingValues,
    onNavigateToBack: () -> Boolean,
) {
    PendingFriendsScreen(
        padding = padding,
        onNavigateToBack = onNavigateToBack,
    )
}
