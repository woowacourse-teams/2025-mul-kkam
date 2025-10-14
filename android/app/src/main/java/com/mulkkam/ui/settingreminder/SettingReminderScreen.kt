package com.mulkkam.ui.settingreminder

import android.content.Context
import android.view.View
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat.getString
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mulkkam.R
import com.mulkkam.domain.model.result.MulKkamError
import com.mulkkam.ui.custom.snackbar.CustomSnackBar
import com.mulkkam.ui.designsystem.MulkkamTheme
import com.mulkkam.ui.designsystem.White
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.model.MulKkamUiState.Idle.toSuccessDataOrNull
import com.mulkkam.ui.settingreminder.component.ReminderScheduleBottomSheet
import com.mulkkam.ui.settingreminder.component.SettingReminderContainer
import com.mulkkam.ui.settingreminder.component.SettingReminderTopAppBar
import com.mulkkam.ui.settingreminder.model.ReminderUpdateUiState
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingReminderScreen(
    navigateToBack: () -> Unit,
    viewModel: SettingReminderViewModel = viewModel(),
) {
    val context = LocalContext.current
    val view = LocalView.current
    val modalBottomSheetState = rememberModalBottomSheetState()
    val isReminderEnabled by viewModel.isReminderEnabled.collectAsStateWithLifecycle()
    val reminders by viewModel.reminderSchedules.collectAsStateWithLifecycle()
    val reminderUpdateUiState by viewModel.reminderUpdateUiState.collectAsStateWithLifecycle()

    val showBottomSheet = reminderUpdateUiState !is ReminderUpdateUiState.Idle

    LaunchedEffect(Unit) {
        viewModel.onReminderUpdated.collect { state ->
            handleReminderUpdateAction(state, view, context)
        }
    }

    Scaffold(
        topBar = { SettingReminderTopAppBar(navigateToBack) },
        containerColor = White,
    ) { innerPadding ->
        SettingReminderContainer(
            isReminderEnabled = isReminderEnabled.toSuccessDataOrNull() ?: return@Scaffold,
            reminders = reminders.toSuccessDataOrNull() ?: return@Scaffold,
            updateBottomSheetMode = { viewModel.updateReminderUpdateUiState(it) },
            updateReminderEnabled = { viewModel.updateReminderEnabled() },
            removeReminder = { viewModel.removeReminder(it) },
            modifier = Modifier.padding(innerPadding),
        )

        if (showBottomSheet) {
            val currentMode = reminderUpdateUiState
            val initialTime =
                when (currentMode) {
                    is ReminderUpdateUiState.Update -> currentMode.reminderSchedule.schedule
                    is ReminderUpdateUiState.Add, ReminderUpdateUiState.Idle -> LocalTime.now()
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

private fun handleReminderUpdateAction(
    state: MulKkamUiState<Unit>,
    view: View,
    context: Context,
) {
    when (state) {
        is MulKkamUiState.Failure -> {
            if (state.error is MulKkamError.ReminderError.DuplicatedReminderSchedule) {
                CustomSnackBar
                    .make(
                        view,
                        getString(context, R.string.setting_reminder_duplicated_schedule),
                        R.drawable.ic_info_circle,
                    ).show()
            } else {
                CustomSnackBar
                    .make(
                        view,
                        getString(context, R.string.network_check_error),
                        R.drawable.ic_info_circle,
                    ).show()
            }
        }

        is MulKkamUiState.Idle, MulKkamUiState.Loading -> Unit
        is MulKkamUiState.Success<Unit> -> Unit
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
