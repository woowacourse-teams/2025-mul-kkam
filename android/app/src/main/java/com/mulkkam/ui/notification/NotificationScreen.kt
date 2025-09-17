package com.mulkkam.ui.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mulkkam.ui.designsystem.MulkkamTheme
import com.mulkkam.ui.designsystem.White
import com.mulkkam.ui.model.MulKkamUiState.Idle.toSuccessDataOrNull
import com.mulkkam.ui.notification.component.NotificationItem
import com.mulkkam.ui.notification.component.NotificationTopAppBar

@Composable
fun NotificationScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NotificationViewModel = NotificationViewModel(),
) {
    val notifications = viewModel.notifications.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.background(White),
        topBar = { NotificationTopAppBar(onBackClick) },
        containerColor = White,
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding),
        ) {
            val notification = notifications.value.toSuccessDataOrNull() ?: return@LazyColumn

            items(notification.size) { index ->
                NotificationItem(
                    notification = notification[index],
                    onApplySuggestion = {
                        viewModel.applySuggestion(
                            notification[index].id,
                        )
                    },
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
