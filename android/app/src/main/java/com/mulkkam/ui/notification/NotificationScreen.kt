package com.mulkkam.ui.notification

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mulkkam.R
import com.mulkkam.ui.designsystem.Gray400
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.MulkkamTheme
import com.mulkkam.ui.designsystem.White
import com.mulkkam.ui.model.MulKkamUiState.Idle.toSuccessDataOrNull
import com.mulkkam.ui.notification.component.NotificationItemComponent
import com.mulkkam.ui.notification.component.NotificationTopAppBar

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
            Column(
                modifier =
                    Modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_crying_character),
                    modifier = Modifier.size(200.dp),
                    contentDescription = stringResource(R.string.notification_empty_description),
                )
                Spacer(modifier = Modifier.padding(vertical = 20.dp))
                Text(
                    text = stringResource(R.string.notification_empty),
                    style = MulKkamTheme.typography.body2,
                    color = Gray400,
                )
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
