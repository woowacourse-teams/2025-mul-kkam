package com.mulkkam.ui.settingreminder.component

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mulkkam.R
import com.mulkkam.domain.model.reminder.ReminderSchedule
import com.mulkkam.ui.designsystem.Black
import com.mulkkam.ui.designsystem.Gray300
import com.mulkkam.ui.designsystem.Gray400
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.MulkkamTheme
import com.mulkkam.ui.designsystem.White
import java.time.Duration
import java.time.LocalTime
import java.time.temporal.ChronoUnit

private const val HOURS_PER_DAY: Int = 24
private const val NO_HOUR_DIFF: Long = 0
private const val MINUTES_PER_HOUR: Int = 60

@Composable
fun ReminderScheduleItemComponent(
    reminder: ReminderSchedule,
    modifier: Modifier = Modifier,
    currentTime: LocalTime = LocalTime.now(),
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
            painter = painterResource(R.drawable.ic_setting_next),
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
    val diffMinutes =
        Duration
            .between(currentTime.truncatedTo(ChronoUnit.MINUTES), reminderTime)
            .toMinutes()
            .let { if (it < 0) it + HOURS_PER_DAY * MINUTES_PER_HOUR else it }

    val (hours, minutes) = diffMinutes / MINUTES_PER_HOUR to diffMinutes % MINUTES_PER_HOUR

    return stringResource(
        if (hours == NO_HOUR_DIFF) {
            R.string.setting_reminder_minutes_left
        } else {
            R.string.setting_reminder_hours_left
        },
        if (hours == NO_HOUR_DIFF) minutes else hours,
    )
}

@Preview(showBackground = true, name = "1시간 이상 남은 경우")
@Composable
private fun ReminderScheduleItemComponentPreview_OverOneHour() {
    MulkkamTheme {
        ReminderScheduleItemComponent(
            reminder =
                ReminderSchedule(
                    id = 1L,
                    schedule = LocalTime.of(13, 45),
                ),
            currentTime = LocalTime.of(10, 45),
        )
    }
}

@Preview(showBackground = true, name = "1시간 미만으로 남은 경우")
@Composable
private fun ReminderScheduleItemComponentPreview_LessThanOneHour() {
    MulkkamTheme {
        ReminderScheduleItemComponent(
            reminder =
                ReminderSchedule(
                    id = 1L,
                    schedule = LocalTime.of(13, 45),
                ),
            currentTime = LocalTime.of(13, 40),
        )
    }
}
