package com.mulkkam.ui.settingreminder

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mulkkam.ui.designsystem.MulkkamTheme
import com.mulkkam.ui.designsystem.Primary100
import com.mulkkam.ui.designsystem.White
import com.mulkkam.ui.model.MulKkamUiState.Idle.toSuccessDataOrNull
import com.mulkkam.ui.settingreminder.component.ReminderScheduleBottomSheet
import com.mulkkam.ui.settingreminder.component.ReminderScheduleItem
import com.mulkkam.ui.settingreminder.component.ReminderSwitchRow
import com.mulkkam.ui.settingreminder.component.SettingReminderTopAppBar
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingReminderScreen(
    navigateToBack: () -> Unit,
    viewModel: SettingReminderViewModel = viewModel(),
) {
    var bottomSheetMode by rememberSaveable { mutableStateOf<ReminderMode?>(null) }
    val modalBottomSheetState = rememberModalBottomSheetState()
    val isReminderEnabled by viewModel.isReminderEnabled.collectAsStateWithLifecycle()
    val reminders by viewModel.reminderSchedules.collectAsStateWithLifecycle()

    val showBottomSheet = bottomSheetMode != null

    Scaffold(
        topBar = { SettingReminderTopAppBar(navigateToBack) },
        containerColor = White,
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding),
        ) {
            ReminderSwitchRow(
                checked = isReminderEnabled.toSuccessDataOrNull() ?: return@Column,
            ) { viewModel.updateReminderEnabled() }

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
            ) {
                val reminder = reminders.toSuccessDataOrNull() ?: return@LazyColumn
                items(
                    reminder.size,
                    key = { reminder[it].id },
                ) { index ->
                    ReminderScheduleItem(
                        reminder = reminder[index],
                        onRemove = { viewModel.removeReminder(reminder[index].id) },
                        modifier =
                            Modifier
                                .clickable {
                                    bottomSheetMode = ReminderMode.UPDATE(reminder[index])
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
                            bottomSheetMode = ReminderMode.ADD
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

        if (showBottomSheet) {
            val currentMode = bottomSheetMode
            val initialTime =
                when (currentMode) {
                    is ReminderMode.UPDATE -> currentMode.reminderSchedule.schedule
                    else -> LocalTime.now()
                }

            ReminderScheduleBottomSheet(
                sheetState = modalBottomSheetState,
                onDismiss = { bottomSheetMode = null },
                onSelected = { selectedTime ->
                    handleReminderAction(currentMode, selectedTime, viewModel)
                    bottomSheetMode = null
                },
                currentTime = initialTime,
            )
        }
    }
}

private fun handleReminderAction(
    mode: ReminderMode?,
    selectedTime: LocalTime,
    viewModel: SettingReminderViewModel,
) {
    when (mode) {
        is ReminderMode.UPDATE -> {
            viewModel.updateReminder(mode.reminderSchedule.copy(schedule = selectedTime))
        }

        is ReminderMode.ADD -> {
            viewModel.addReminder(selectedTime)
        }

        null -> Unit
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingRemindScreenPreview() {
    MulkkamTheme {
        SettingReminderScreen(
            navigateToBack = {},
        )
    }
}
