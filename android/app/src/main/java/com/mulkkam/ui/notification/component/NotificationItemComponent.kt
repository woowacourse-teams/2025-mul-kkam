package com.mulkkam.ui.notification.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mulkkam.R
import com.mulkkam.domain.model.notification.Notification
import com.mulkkam.domain.model.notification.NotificationType
import com.mulkkam.ui.designsystem.MulkkamTheme
import com.mulkkam.ui.designsystem.Secondary200
import java.time.LocalDateTime

@Composable
fun NotificationItemComponent(
    notification: Notification,
    onApplySuggestion: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val dismissState =
        rememberSwipeToDismissBoxState(
            confirmValueChange = { dismissValue ->
                dismissValue == SwipeToDismissBoxValue.EndToStart
            },
            positionalThreshold = { it * 0.7f },
        )
    val color =
        when (dismissState.dismissDirection) {
            SwipeToDismissBoxValue.EndToStart -> Secondary200
            else -> Color.Transparent
        }

    LaunchedEffect(dismissState.currentValue) {
        if (dismissState.currentValue == SwipeToDismissBoxValue.EndToStart) {
            onRemove()
        }
    }

    SwipeToDismissBox(
        modifier = modifier,
        state = dismissState,
        backgroundContent = {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(color)
                        .padding(end = 18.dp),
                contentAlignment = Alignment.CenterEnd,
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_notification_delete),
                    modifier = Modifier.size(24.dp),
                    contentDescription = stringResource(R.string.notification_delete_description),
                )
            }
        },
        enableDismissFromStartToEnd = false,
    ) {
        NotificationItem(notification, onApplySuggestion)
    }
}

@Preview(showBackground = true, name = "공지 알림 프리뷰")
@Composable
private fun NoticeNotificationItemComponentPreview() {
    MulkkamTheme {
        NotificationItemComponent(
            notification =
                Notification(
                    id = 1,
                    title = "공지 알림입니다 !",
                    type = NotificationType.NOTICE,
                    createdAt = LocalDateTime.now(),
                    recommendedTargetAmount = null,
                    isRead = true,
                    applyRecommendAmount = null,
                ),
            onApplySuggestion = {},
            onRemove = { },
        )
    }
}

@Preview(showBackground = true, name = "제안 알림 프리뷰")
@Composable
private fun SuggestionNotificationItemComponentPreview() {
    MulkkamTheme {
        NotificationItemComponent(
            notification =
                Notification(
                    id = 1,
                    title = "제안 알림입니다 !",
                    type = NotificationType.SUGGESTION,
                    createdAt = LocalDateTime.now(),
                    recommendedTargetAmount = 100,
                    isRead = true,
                    applyRecommendAmount = false,
                ),
            onApplySuggestion = {},
            onRemove = { },
        )
    }
}

@Preview(showBackground = true, name = "읽지 않은 알림 프리뷰")
@Composable
private fun UnreadNotificationItemComponentPreview() {
    MulkkamTheme {
        NotificationItemComponent(
            notification =
                Notification(
                    id = 1,
                    title = "안 읽은 알림입니다 !",
                    type = NotificationType.SUGGESTION,
                    createdAt = LocalDateTime.now(),
                    recommendedTargetAmount = 100,
                    isRead = false,
                    applyRecommendAmount = false,
                ),
            onApplySuggestion = {},
            onRemove = { },
        )
    }
}
