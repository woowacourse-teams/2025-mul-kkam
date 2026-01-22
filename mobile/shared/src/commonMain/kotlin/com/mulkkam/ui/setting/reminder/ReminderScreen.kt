package com.mulkkam.ui.setting.reminder

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mulkkam.domain.model.result.MulKkamError
import com.mulkkam.ui.component.showMulKkamSnackbar
import com.mulkkam.ui.designsystem.White
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.model.toSuccessDataOrNull
import com.mulkkam.ui.setting.reminder.component.ReminderScheduleBottomSheet
import com.mulkkam.ui.setting.reminder.component.SettingReminderContainer
import com.mulkkam.ui.setting.reminder.component.SettingReminderTopAppBar
import com.mulkkam.ui.setting.reminder.model.ReminderUpdateUiState
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import mulkkam.shared.generated.resources.Res
import mulkkam.shared.generated.resources.ic_info_circle
import mulkkam.shared.generated.resources.network_check_error
import mulkkam.shared.generated.resources.setting_reminder_duplicated_schedule
import org.jetbrains.compose.resources.getString
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun ReminderScreen(
    padding: PaddingValues,
    navigateToBack: () -> Unit,
    snackbarHostState: SnackbarHostState,
    viewModel: SettingReminderViewModel = koinViewModel(),
) {
    val modalBottomSheetState = rememberModalBottomSheetState()
    val isReminderEnabled by viewModel.isReminderEnabled.collectAsStateWithLifecycle()
    val reminders by viewModel.reminderSchedules.collectAsStateWithLifecycle()
    val reminderUpdateUiState by viewModel.reminderUpdateUiState.collectAsStateWithLifecycle()

    val showBottomSheet = reminderUpdateUiState !is ReminderUpdateUiState.Idle

    LaunchedEffect(snackbarHostState) {
        viewModel.onReminderUpdated.collect { state ->
            if (state !is MulKkamUiState.Failure) return@collect
            if (state.error is MulKkamError.ReminderError.DuplicatedReminderSchedule) {
                snackbarHostState.showMulKkamSnackbar(
                    message = getString(resource = Res.string.setting_reminder_duplicated_schedule),
                    iconResource = Res.drawable.ic_info_circle,
                )
            } else {
                snackbarHostState.showMulKkamSnackbar(
                    message = getString(resource = Res.string.network_check_error),
                    iconResource = Res.drawable.ic_info_circle,
                )
            }
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        topBar = { SettingReminderTopAppBar(navigateToBack) },
        containerColor = White,
        modifier = Modifier.fillMaxSize().background(White).padding(padding),
    ) { innerPadding ->
        SettingReminderContainer(
            isReminderEnabled = isReminderEnabled.toSuccessDataOrNull() ?: return@Scaffold,
            reminders = reminders.toSuccessDataOrNull() ?: return@Scaffold,
            updateBottomSheetMode = viewModel::updateReminderUpdateUiState,
            updateReminderEnabled = viewModel::updateReminderEnabled,
            removeReminder = viewModel::deleteReminder,
            modifier = Modifier.padding(innerPadding),
        )

        if (showBottomSheet) {
            val initialTime =
                when (val currentMode = reminderUpdateUiState) {
                    is ReminderUpdateUiState.Update -> {
                        currentMode.reminderSchedule.schedule
                    }

                    is ReminderUpdateUiState.Add, ReminderUpdateUiState.Idle -> {
                        Clock.System
                            .now()
                            .toLocalDateTime(TimeZone.currentSystemDefault())
                            .time
                    }
                }

            ReminderScheduleBottomSheet(
                sheetState = modalBottomSheetState,
                onDismiss = { viewModel.updateReminderUpdateUiState(ReminderUpdateUiState.Idle) },
                onSelected = { selectedTime ->
                    viewModel.handleReminderUpdateAction(selectedTime)
                },
                currentTime = initialTime,
            )
        }
    }
}
