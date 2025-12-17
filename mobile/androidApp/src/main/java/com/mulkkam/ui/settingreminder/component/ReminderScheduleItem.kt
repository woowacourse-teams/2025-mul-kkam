package com.mulkkam.ui.settingreminder.component

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
import com.mulkkam.domain.model.reminder.ReminderSchedule
import com.mulkkam.ui.designsystem.MulkkamTheme
import com.mulkkam.ui.designsystem.Secondary200
import java.time.LocalTime

@Composable
fun ReminderScheduleItem(
    reminder: ReminderSchedule,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
    currentTime: LocalTime = LocalTime.now(),
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
                    painter = painterResource(R.drawable.ic_common_delete),
                    modifier = Modifier.size(24.dp),
                    contentDescription = stringResource(R.string.setting_reminder_delete_description),
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
    MulkkamTheme {
        ReminderScheduleItem(
            reminder = ReminderSchedule(1L, LocalTime.of(13, 0)),
            onRemove = {},
        )
    }
}
