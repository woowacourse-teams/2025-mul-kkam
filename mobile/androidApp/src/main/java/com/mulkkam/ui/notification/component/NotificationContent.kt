package com.mulkkam.ui.notification.component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mulkkam.R
import com.mulkkam.domain.model.notification.Notification
import com.mulkkam.ui.designsystem.Gray200
import com.mulkkam.ui.designsystem.Gray400
import com.mulkkam.ui.designsystem.MulKkamTheme
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private const val ONE_MINUTE: Long = 1L
private const val ONE_HOUR: Long = 1L
private const val ONE_DAY: Long = 1L
private const val TWO_DAYS: Long = 2L
private val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")

@Composable
fun NotificationContent(notification: Notification) {
    Text(
        text = notification.title,
        style = MulKkamTheme.typography.body5,
        color = Gray400,
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
        text = notification.createdAt.toRelativeTimeString(),
        style = MulKkamTheme.typography.body5,
        color = Gray200,
    )
}

@Composable
private fun LocalDateTime.toRelativeTimeString(): String {
    val now = LocalDateTime.now()
    val duration = Duration.between(this, now)

    return when {
        duration.toMinutes() < ONE_MINUTE ->
            stringResource(
                R.string.notification_just_now,
            )

        duration.toHours() < ONE_HOUR ->
            stringResource(
                R.string.notification_minutes_ago,
            ).format(duration.toMinutes())

        duration.toDays() < ONE_DAY ->
            stringResource(
                R.string.notification_hours_ago,
            ).format(duration.toHours())

        duration.toDays() < TWO_DAYS ->
            stringResource(
                R.string.notification_one_day_ago,
            )

        else -> this.format(dateTimeFormatter)
    }
}
