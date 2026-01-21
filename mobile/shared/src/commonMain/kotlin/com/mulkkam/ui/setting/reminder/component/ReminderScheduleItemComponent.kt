package com.mulkkam.ui.setting.reminder.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mulkkam.domain.model.reminder.ReminderSchedule
import com.mulkkam.ui.designsystem.Black
import com.mulkkam.ui.designsystem.Gray300
import com.mulkkam.ui.designsystem.Gray400
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.White
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import mulkkam.shared.generated.resources.Res
import mulkkam.shared.generated.resources.ic_common_next
import mulkkam.shared.generated.resources.setting_reminder_hours_left
import mulkkam.shared.generated.resources.setting_reminder_minutes_left
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

private const val HOURS_PER_DAY: Int = 24
private const val NO_HOUR_DIFF: Long = 0
private const val MINUTES_PER_HOUR: Int = 60

@OptIn(ExperimentalTime::class)
@Composable
fun ReminderScheduleItemComponent(
    reminder: ReminderSchedule,
    modifier: Modifier = Modifier,
    currentTime: LocalTime =
        Clock.System
            .now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .time,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .background(White)
                .padding(start = 24.dp, end = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp),
        ) {
            Text(
                text = reminder.schedule.toString(),
                style = MulKkamTheme.typography.headline1,
                color = Black,
            )
            Text(
                text = formatRemainingTime(currentTime, reminder.schedule),
                style = MulKkamTheme.typography.label2,
                color = Gray300,
            )
        }
        Icon(
            painter = painterResource(resource = Res.drawable.ic_common_next),
            contentDescription = null,
            tint = Gray400,
        )
    }
}

@Composable
private fun formatRemainingTime(
    currentTime: LocalTime,
    reminderTime: LocalTime,
): String {
    val currentMinutes = currentTime.hour * MINUTES_PER_HOUR + currentTime.minute
    val reminderMinutes = reminderTime.hour * MINUTES_PER_HOUR + reminderTime.minute
    val diffMinutes =
        (reminderMinutes - currentMinutes).let {
            if (it < 0) it + HOURS_PER_DAY * MINUTES_PER_HOUR else it
        }

    val hours = diffMinutes / MINUTES_PER_HOUR
    val minutes = diffMinutes % MINUTES_PER_HOUR

    return stringResource(
        if (hours == NO_HOUR_DIFF.toInt()) {
            Res.string.setting_reminder_minutes_left
        } else {
            Res.string.setting_reminder_hours_left
        },
        if (hours == NO_HOUR_DIFF.toInt()) minutes else hours,
    )
}

@Preview(showBackground = true, name = "1시간 이상 남은 경우")
@Composable
private fun ReminderScheduleItemComponentPreview_OverOneHour() {
    MulKkamTheme {
        ReminderScheduleItemComponent(
            reminder =
                ReminderSchedule(
                    id = 1L,
                    schedule = LocalTime(13, 45),
                ),
            currentTime = LocalTime(10, 45),
        )
    }
}

@Preview(showBackground = true, name = "1시간 미만으로 남은 경우")
@Composable
private fun ReminderScheduleItemComponentPreview_LessThanOneHour() {
    MulKkamTheme {
        ReminderScheduleItemComponent(
            reminder =
                ReminderSchedule(
                    id = 1L,
                    schedule = LocalTime(13, 45),
                ),
            currentTime = LocalTime(13, 40),
        )
    }
}
