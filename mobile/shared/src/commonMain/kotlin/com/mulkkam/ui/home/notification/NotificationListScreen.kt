package com.mulkkam.ui.home.notification

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mulkkam.domain.model.notification.Notification
import com.mulkkam.ui.home.notification.component.LoadMoreButton
import com.mulkkam.ui.home.notification.component.NotificationItemComponent
import com.mulkkam.ui.model.MulKkamUiState

@Composable
fun NotificationListScreen(
    notifications: List<Notification>,
    loadMoreState: MulKkamUiState<Unit>,
    listState: LazyListState,
    onApplySuggestion: (Long) -> Unit,
    onRemoveNotification: (Long) -> Unit,
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        state = listState,
    ) {
        items(
            count = notifications.size,
            key = { notifications[it].id },
        ) { index ->
            val notification = notifications[index]

            NotificationItemComponent(
                notification = notification,
                onApplySuggestion = { onApplySuggestion(notification.id) },
                onRemove = { onRemoveNotification(notification.id) },
                modifier = Modifier.animateItem(),
            )
        }

        if (loadMoreState is MulKkamUiState.Failure) {
            item {
                LoadMoreButton(onClick = onLoadMore)
            }
        }
    }
}
