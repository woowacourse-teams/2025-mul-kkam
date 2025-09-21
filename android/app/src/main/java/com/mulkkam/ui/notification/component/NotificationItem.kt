package com.mulkkam.ui.notification.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mulkkam.R
import com.mulkkam.domain.model.notification.Notification
import com.mulkkam.domain.model.notification.NotificationType
import com.mulkkam.domain.model.notification.NotificationType.NOTICE
import com.mulkkam.domain.model.notification.NotificationType.REMIND
import com.mulkkam.domain.model.notification.NotificationType.SUGGESTION
import com.mulkkam.ui.designsystem.Black
import com.mulkkam.ui.designsystem.Gray10
import com.mulkkam.ui.designsystem.Gray100
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.Primary10
import com.mulkkam.ui.designsystem.White

@Composable
fun NotificationItem(
    notification: Notification,
    onApplySuggestion: () -> Unit,
) {
    val backgroundColor = if (notification.isRead) White else Primary10
    var applied by rememberSaveable { mutableStateOf(notification.applyRecommendAmount) }

    Row(
        modifier =
            Modifier
                .background(backgroundColor)
                .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Image(
            modifier = Modifier.size(38.dp, 38.dp),
            painter = painterResource(getNotificationIcon(notification.type)),
            contentDescription = stringResource(R.string.notification_item_icon_description),
        )
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start,
        ) {
            NotificationContent(notification)

            if (notification.type == SUGGESTION && applied == false) {
                ApplyButton(
                    modifier = Modifier.align(Alignment.End),
                    onClick = {
                        onApplySuggestion()
                        applied = true
                    },
                )
            }
        }
    }
}

private fun getNotificationIcon(notificationType: NotificationType) =
    when (notificationType) {
        SUGGESTION -> R.drawable.ic_notification_suggestion
        REMIND -> R.drawable.ic_notification_remind
        NOTICE -> R.drawable.ic_notification_notice
    }

@Composable
private fun ApplyButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(4.dp),
        border = BorderStroke(1.dp, Gray100),
        contentPadding = PaddingValues(0.dp),
        colors =
            ButtonDefaults.outlinedButtonColors(
                containerColor = Gray10,
                contentColor = Black,
            ),
        modifier = modifier.size(height = 28.dp, width = 94.dp),
    ) {
        Text(
            text = stringResource(R.string.notification_apply_target_amount),
            color = Black,
            style = MulKkamTheme.typography.label2,
        )
    }
}
