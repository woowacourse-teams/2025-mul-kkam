package com.mulkkam.ui.util.extensions

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember

@Composable
fun LazyListState.OnLoadMore(
    isLoadMoreEnabled: Boolean = true,
    preloadThreshold: Int = 10,
    action: () -> Unit,
) {
    if (!isLoadMoreEnabled) return
    val reached by remember {
        derivedStateOf {
            reachedBottom(preloadThreshold = preloadThreshold)
        }
    }
    LaunchedEffect(reached) {
        if (reached) action()
    }
}

private fun LazyListState.reachedBottom(preloadThreshold: Int): Boolean {
    val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()
    return lastVisibleItem?.index != 0 &&
        (lastVisibleItem?.index ?: 0) >= layoutInfo.totalItemsCount - (preloadThreshold + 1)
}
