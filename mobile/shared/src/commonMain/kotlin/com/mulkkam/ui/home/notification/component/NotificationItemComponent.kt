package com.mulkkam.ui.home.notification.component

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
import androidx.compose.ui.unit.dp
import com.mulkkam.domain.model.notification.Notification
import com.mulkkam.domain.model.notification.NotificationType
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.Secondary200
import kotlinx.coroutines.delay
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import mulkkam.shared.generated.resources.Res
import mulkkam.shared.generated.resources.ic_common_delete
import mulkkam.shared.generated.resources.notification_delete_description
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Composable
fun NotificationItemComponent(
    notification: Notification,
    onApplySuggestion: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val dismissState =
        rememberSwipeToDismissBoxState(
            positionalThreshold = { it * 0.7f },
        )
    val color =
        when (dismissState.dismissDirection) {
            SwipeToDismissBoxValue.EndToStart -> Secondary200
            else -> Color.Transparent
        }

    LaunchedEffect(dismissState.currentValue) {
        if (dismissState.currentValue == SwipeToDismissBoxValue.EndToStart) {
            delay(300)
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
                    painter = painterResource(resource = Res.drawable.ic_common_delete),
                    modifier = Modifier.size(24.dp),
                    contentDescription = stringResource(resource = Res.string.notification_delete_description),
                )
            }
        },
        enableDismissFromStartToEnd = false,
    ) {
        NotificationItem(notification, onApplySuggestion)
    }
}

@OptIn(ExperimentalTime::class)
@Preview(showBackground = true, name = "공지 알림 프리뷰")
@Composable
private fun NoticeNotificationItemComponentPreview() {
    MulKkamTheme {
        NotificationItemComponent(
            notification =
                Notification(
                    id = 1,
                    title = "공지 알림입니다 !",
                    type = NotificationType.NOTICE,
                    createdAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
                    recommendedTargetAmount = null,
                    isRead = true,
                    applyRecommendAmount = null,
                ),
            onApplySuggestion = {},
            onRemove = { },
        )
    }
}

@OptIn(ExperimentalTime::class)
@Preview(showBackground = true, name = "제안 알림 프리뷰")
@Composable
private fun SuggestionNotificationItemComponentPreview() {
    MulKkamTheme {
        NotificationItemComponent(
            notification =
                Notification(
                    id = 1,
                    title = "제안 알림입니다 !",
                    type = NotificationType.SUGGESTION,
                    createdAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
                    recommendedTargetAmount = 100,
                    isRead = true,
                    applyRecommendAmount = false,
                ),
            onApplySuggestion = {},
            onRemove = { },
        )
    }
}

@OptIn(ExperimentalTime::class)
@Preview(showBackground = true, name = "읽지 않은 알림 프리뷰")
@Composable
private fun UnreadNotificationItemComponentPreview() {
    MulKkamTheme {
        NotificationItemComponent(
            notification =
                Notification(
                    id = 1,
                    title = "안 읽은 알림입니다 !",
                    type = NotificationType.SUGGESTION,
                    createdAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
                    recommendedTargetAmount = 100,
                    isRead = false,
                    applyRecommendAmount = false,
                ),
            onApplySuggestion = {},
            onRemove = { },
        )
    }
}
