package com.mulkkam.ui.home.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mulkkam.ui.component.showMulKkamSnackbar
import com.mulkkam.ui.designsystem.White
import com.mulkkam.ui.home.notification.component.LoadMoreButton
import com.mulkkam.ui.home.notification.component.NotificationItemComponent
import com.mulkkam.ui.home.notification.component.NotificationShimmerItem
import com.mulkkam.ui.home.notification.component.NotificationTopAppBar
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.model.toSuccessDataOrNull
import com.mulkkam.ui.util.LoadingShimmerEffect
import com.mulkkam.ui.util.extensions.OnLoadMore
import com.mulkkam.ui.util.extensions.collectWithLifecycle
import mulkkam.shared.generated.resources.Res
import mulkkam.shared.generated.resources.ic_alert_circle
import mulkkam.shared.generated.resources.ic_terms_all_check_on
import mulkkam.shared.generated.resources.notification_apply_failed
import mulkkam.shared.generated.resources.notification_apply_success
import org.jetbrains.compose.resources.getString
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NotificationScreen(
    padding: PaddingValues,
    navigateToBack: () -> Boolean,
    snackbarHostState: SnackbarHostState,
    state: LazyListState = rememberLazyListState(),
    viewModel: NotificationViewModel = koinViewModel(),
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val loadMoreState by viewModel.loadUiState.collectAsStateWithLifecycle()
    state.OnLoadMore(action = { viewModel.loadMore() })

    viewModel.onApplySuggestion.collectWithLifecycle(lifecycleOwner) { state ->
        handleApplySuggestion(state, snackbarHostState)
    }

    Scaffold(
        topBar = { NotificationTopAppBar { navigateToBack() } },
        containerColor = White,
        modifier = Modifier.background(White).padding(padding),
    ) { innerPadding ->
        if (notifications.toSuccessDataOrNull()?.isEmpty() == true) {
            EmptyNotificationScreen(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
            )
        }

        when (notifications) {
            is MulKkamUiState.Loading -> {
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

            is MulKkamUiState.Success<*> -> {
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

            is MulKkamUiState.Failure -> {
                EmptyNotificationScreen(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                )
            }

            is MulKkamUiState.Idle -> Unit
        }
    }
}

private suspend fun handleApplySuggestion(
    state: MulKkamUiState<Unit>,
    snackbarHostState: SnackbarHostState,
) {
    when (state) {
        is MulKkamUiState.Success -> {
            snackbarHostState
                .showMulKkamSnackbar(
                    message = getString(resource = Res.string.notification_apply_success),
                    iconResource = Res.drawable.ic_terms_all_check_on,
                )
        }

        is MulKkamUiState.Idle, MulKkamUiState.Loading -> Unit
        is MulKkamUiState.Failure -> {
            snackbarHostState
                .showMulKkamSnackbar(
                    message = getString(resource = Res.string.notification_apply_failed),
                    iconResource = Res.drawable.ic_alert_circle,
                )
        }
    }
}
