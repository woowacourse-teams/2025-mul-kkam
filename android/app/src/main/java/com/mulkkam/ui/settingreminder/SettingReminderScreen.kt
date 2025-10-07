package com.mulkkam.ui.settingreminder

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mulkkam.ui.designsystem.MulkkamTheme
import com.mulkkam.ui.designsystem.White
import com.mulkkam.ui.model.MulKkamUiState.Idle.toSuccessDataOrNull
import com.mulkkam.ui.settingreminder.component.ReminderScheduleBottomSheet
import com.mulkkam.ui.settingreminder.component.SettingReminderComponent
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
        SettingReminderComponent(
            isReminderEnabled = isReminderEnabled.toSuccessDataOrNull() ?: return@Scaffold,
            reminders = reminders.toSuccessDataOrNull() ?: return@Scaffold,
            updateBottomSheetMode = { bottomSheetMode = it },
            updateReminderEnabled = { viewModel.updateReminderEnabled() },
            removeReminder = { viewModel.removeReminder(it) },
            modifier = Modifier.padding(innerPadding),
        )

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
