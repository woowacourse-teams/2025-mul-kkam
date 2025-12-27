package com.mulkkam.ui.settingreminder

import android.content.Context
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat.getString
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mulkkam.R
import com.mulkkam.domain.model.result.MulKkamError
import com.mulkkam.ui.component.MulKkamSnackbarHost
import com.mulkkam.ui.component.showMulKkamSnackbar
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.White
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.model.MulKkamUiState.Idle.toSuccessDataOrNull
import com.mulkkam.ui.settingreminder.component.ReminderScheduleBottomSheet
import com.mulkkam.ui.settingreminder.component.SettingReminderContainer
import com.mulkkam.ui.settingreminder.component.SettingReminderTopAppBar
import com.mulkkam.ui.settingreminder.model.ReminderUpdateUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.datetime.toKotlinLocalTime
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingReminderScreen(
    navigateToBack: () -> Unit,
    viewModel: SettingReminderViewModel = koinViewModel(),
) {
    val context = LocalContext.current
    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
    val modalBottomSheetState = rememberModalBottomSheetState()
    val isReminderEnabled by viewModel.isReminderEnabled.collectAsStateWithLifecycle()
    val reminders by viewModel.reminderSchedules.collectAsStateWithLifecycle()
    val reminderUpdateUiState by viewModel.reminderUpdateUiState.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()

    val showBottomSheet = reminderUpdateUiState !is ReminderUpdateUiState.Idle

    LaunchedEffect(snackbarHostState) {
        viewModel.onReminderUpdated.collect { state ->
            handleReminderUpdateAction(
                state = state,
                snackbarHostState = snackbarHostState,
                context = context,
                coroutineScope = coroutineScope,
            )
        }
    }

    Scaffold(
        topBar = { SettingReminderTopAppBar(navigateToBack) },
        containerColor = White,
        snackbarHost = { MulKkamSnackbarHost(hostState = snackbarHostState) },
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
            val currentMode = reminderUpdateUiState
            val initialTime =
                when (currentMode) {
                    is ReminderUpdateUiState.Update -> {
                        currentMode.reminderSchedule.schedule
                    }

                    is ReminderUpdateUiState.Add, ReminderUpdateUiState.Idle -> {
                        java.time.LocalTime
                            .now()
                            .toKotlinLocalTime()
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

private fun handleReminderUpdateAction(
    state: MulKkamUiState<Unit>,
    snackbarHostState: SnackbarHostState,
    context: Context,
    coroutineScope: CoroutineScope,
) {
    if (state !is MulKkamUiState.Failure) return
    if (state.error is MulKkamError.ReminderError.DuplicatedReminderSchedule) {
        coroutineScope.launch {
            snackbarHostState.showMulKkamSnackbar(
                message = getString(context, R.string.setting_reminder_duplicated_schedule),
                iconResourceId = R.drawable.ic_info_circle,
            )
        }
    } else {
        coroutineScope.launch {
            snackbarHostState.showMulKkamSnackbar(
                message = getString(context, R.string.network_check_error),
                iconResourceId = R.drawable.ic_info_circle,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingRemindScreenPreview() {
    MulKkamTheme {
        SettingReminderScreen(
            navigateToBack = {},
        )
    }
}
