package com.mulkkam.ui.searchmembers

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable

@Composable
fun SearchMembersRoute(
    padding: PaddingValues,
    onNavigateToBack: () -> Boolean,
    snackbarHostState: SnackbarHostState,
    onFriendAccepted: () -> Unit = {},
) {
    SearchMembersScreen(
        padding = padding,
        onNavigateToBack = onNavigateToBack,
        snackbarHostState = snackbarHostState,
        onFriendAccepted = onFriendAccepted,
    )
}
