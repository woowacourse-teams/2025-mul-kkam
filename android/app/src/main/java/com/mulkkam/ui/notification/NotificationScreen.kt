package com.mulkkam.ui.notification

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
    onBackClick: () -> Unit,
    viewModel: NotificationViewModel = NotificationViewModel(),
) {
    val notifications = viewModel.notifications.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { NotificationTopAppBar(onBackClick) },
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

@Preview(showBackground = true)
@Composable
private fun NotificationScreenPreview() {
    MulkkamTheme {
        Column {
            NotificationScreen(
                onBackClick = {},
            )
        }
    }
}
