package com.mulkkam.ui.notification

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mulkkam.ui.designsystem.MulkkamTheme
import com.mulkkam.ui.designsystem.White
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.model.MulKkamUiState.Idle.toSuccessDataOrNull
import com.mulkkam.ui.notification.component.NotificationItemComponent
import com.mulkkam.ui.notification.component.NotificationShimmerItem
import com.mulkkam.ui.notification.component.NotificationTopAppBar
import com.mulkkam.ui.util.LoadingShimmerEffect

@Composable
fun NotificationScreen(
    navigateToBack: () -> Unit,
    state: LazyListState = rememberLazyListState(),
    viewModel: NotificationViewModel = viewModel(),
) {
    val notifications = viewModel.notifications.collectAsStateWithLifecycle()
    state.onLoadMore(action = { viewModel.loadMore() })

    Scaffold(
        topBar = { NotificationTopAppBar(navigateToBack) },
        containerColor = White,
    ) { innerPadding ->
        if (notifications.value.toSuccessDataOrNull()?.isEmpty() == true) {
            EmptyNotificationScreen(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
            )
        }

        if (notifications.value == MulKkamUiState.Loading) {
            Column(
                modifier = Modifier.padding(innerPadding),
            ) {
                repeat(7) {
                    LoadingShimmerEffect {
                        NotificationShimmerItem(it)
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier.padding(innerPadding),
            state = state,
        ) {
            val notification = notifications.value.toSuccessDataOrNull() ?: return@LazyColumn

            items(
                notification.size,
                key = { notification[it].id },
            ) { index ->
                NotificationItemComponent(
                    notification = notification[index],
                    onApplySuggestion = {
                        viewModel.applySuggestion(
                            notification[index].id,
                        )
                    },
                    onRemove = {
                        viewModel.deleteNotification(notification[index].id)
                    },
                    modifier = Modifier.animateItem(),
                )
            }
        }
    }
}

private fun LazyListState.reachedBottom(preloadThreshold: Int): Boolean {
    val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()
    return lastVisibleItem?.index != 0 &&
        lastVisibleItem?.index == layoutInfo.totalItemsCount - (preloadThreshold + 1)
}

@SuppressLint("ComposableNaming")
@Composable
private fun LazyListState.onLoadMore(
    preloadThreshold: Int = 6,
    action: () -> Unit,
) {
    val reached by remember {
        derivedStateOf {
            reachedBottom(preloadThreshold = preloadThreshold)
        }
    }
    LaunchedEffect(reached) {
        if (reached) action()
    }
}

@Preview(showBackground = true)
@Composable
private fun NotificationScreenPreview() {
    MulkkamTheme {
        Column {
            NotificationScreen(
                navigateToBack = {},
            )
        }
    }
}
