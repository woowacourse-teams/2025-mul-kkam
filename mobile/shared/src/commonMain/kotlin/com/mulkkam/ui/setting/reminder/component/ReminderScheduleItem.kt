package com.mulkkam.ui.setting.reminder.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxDefaults.positionalThreshold
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mulkkam.domain.model.reminder.ReminderSchedule
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.Secondary200
import kotlinx.coroutines.delay
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import mulkkam.shared.generated.resources.Res
import mulkkam.shared.generated.resources.ic_common_delete
import mulkkam.shared.generated.resources.setting_reminder_delete_description
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun ReminderScheduleItem(
    reminder: ReminderSchedule,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
    currentTime: LocalTime =
        Clock.System
            .now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .time,
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
                    contentDescription = stringResource(resource = Res.string.setting_reminder_delete_description),
                )
            }
        },
        enableDismissFromStartToEnd = false,
    ) {
        ReminderScheduleItemComponent(
            reminder = reminder,
            currentTime = currentTime,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RemindItemPreview() {
    MulKkamTheme {
        ReminderScheduleItem(
            reminder = ReminderSchedule(1L, LocalTime(13, 0)),
            onRemove = {},
        )
    }
}
