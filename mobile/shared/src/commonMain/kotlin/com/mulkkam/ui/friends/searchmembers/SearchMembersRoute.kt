package com.mulkkam.ui.friends.searchmembers

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable

@Composable
fun SearchMembersRoute(
    padding: PaddingValues,
    onNavigateToBack: () -> Boolean,
) {
    SearchMembersScreen(
        padding = padding,
        onNavigateToBack = onNavigateToBack,
    )
}
