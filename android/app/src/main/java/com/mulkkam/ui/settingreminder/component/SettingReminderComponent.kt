package com.mulkkam.ui.settingreminder.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.mulkkam.domain.model.reminder.ReminderSchedule
import com.mulkkam.ui.designsystem.Primary100
import com.mulkkam.ui.designsystem.White
import com.mulkkam.ui.settingreminder.ReminderMode

@Composable
fun SettingReminderComponent(
    isReminderEnabled: Boolean,
    reminders: List<ReminderSchedule>,
    updateBottomSheetMode: (ReminderMode) -> Unit,
    updateReminderEnabled: () -> Unit,
    removeReminder: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        ReminderSwitchRow(
            checked = isReminderEnabled,
        ) { updateReminderEnabled() }

        if (isReminderEnabled == true) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
            ) {
                val reminder = reminders
                items(
                    reminder.size,
                    key = { reminder[it].id },
                ) { index ->
                    ReminderScheduleItem(
                        reminder = reminder[index],
                        onRemove = { removeReminder(reminder[index].id) },
                        modifier =
                            Modifier
                                .clickable {
                                    updateBottomSheetMode(ReminderMode.UPDATE(reminder[index]))
                                }.animateItem(),
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .padding(horizontal = 24.dp)
                        .background(Primary100)
                        .clickable {
                            updateBottomSheetMode(ReminderMode.ADD)
                        },
            ) {
                Icon(
                    painter = painterResource(com.mulkkam.R.drawable.ic_setting_add),
                    contentDescription = "",
                    modifier = Modifier.align(Alignment.Center),
                    tint = White,
                )
            }
        }
    }
}
