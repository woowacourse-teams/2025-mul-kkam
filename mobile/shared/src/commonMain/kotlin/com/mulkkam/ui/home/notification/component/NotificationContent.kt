package com.mulkkam.ui.home.notification.component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mulkkam.domain.model.notification.Notification
import com.mulkkam.ui.designsystem.Gray200
import com.mulkkam.ui.designsystem.Gray400
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.util.extensions.format
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import mulkkam.shared.generated.resources.Res
import mulkkam.shared.generated.resources.notification_hours_ago
import mulkkam.shared.generated.resources.notification_just_now
import mulkkam.shared.generated.resources.notification_minutes_ago
import mulkkam.shared.generated.resources.notification_one_day_ago
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

private const val ONE_MINUTE: Long = 1L
private const val ONE_HOUR: Long = 1L
private const val ONE_DAY: Long = 1L
private const val TWO_DAYS: Long = 2L

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

@OptIn(ExperimentalTime::class)
@Composable
private fun LocalDateTime.toRelativeTimeString(): String {
    val timeZone = TimeZone.currentSystemDefault()
    val nowInstant = Clock.System.now()
    val thisInstant = this.toInstant(timeZone)
    val duration = nowInstant - thisInstant

    return when {
        duration.inWholeMinutes < ONE_MINUTE ->
            stringResource(
                resource = Res.string.notification_just_now,
            )

        duration.inWholeHours < ONE_HOUR ->
            stringResource(
                resource = Res.string.notification_minutes_ago,
                duration.inWholeMinutes,
            )

        duration.inWholeDays < ONE_DAY ->
            stringResource(
                resource = Res.string.notification_hours_ago,
                duration.inWholeHours,
            )

        duration.inWholeDays < TWO_DAYS ->
            stringResource(
                resource = Res.string.notification_one_day_ago,
            )

        else -> this.format("yyyy.MM.dd")
    }
}
