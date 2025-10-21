package com.mulkkam.ui.notification

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mulkkam.ui.designsystem.MulkkamTheme
import com.mulkkam.ui.designsystem.White
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.model.MulKkamUiState.Idle.toSuccessDataOrNull
import com.mulkkam.ui.notification.component.LoadMoreButton
import com.mulkkam.ui.notification.component.NotificationItemComponent
import com.mulkkam.ui.notification.component.NotificationShimmerItem
import com.mulkkam.ui.notification.component.NotificationTopAppBar
import com.mulkkam.ui.util.LoadingShimmerEffect
import com.mulkkam.ui.util.extensions.onLoadMore

@Composable
fun NotificationScreen(
    navigateToBack: () -> Unit,
    state: LazyListState = rememberLazyListState(),
    viewModel: NotificationViewModel = hiltViewModel(),
) {
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val loadMoreState by viewModel.loadUiState.collectAsStateWithLifecycle()
    state.onLoadMore(action = { viewModel.loadMore() })

    Scaffold(
        topBar = { NotificationTopAppBar(navigateToBack) },
        containerColor = White,
    ) { innerPadding ->
        if (notifications.toSuccessDataOrNull()?.isEmpty() == true) {
            EmptyNotificationScreen(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
            )
        }

        if (notifications == MulKkamUiState.Loading) {
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

        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
        ) {
            LazyColumn(
                state = state,
            ) {
                val notification = notifications.toSuccessDataOrNull() ?: return@LazyColumn

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

                if (loadMoreState is MulKkamUiState.Failure) {
                    item {
                        LoadMoreButton { viewModel.loadMore() }
                    }
                }
            }
        }
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
