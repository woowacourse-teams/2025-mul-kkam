package com.mulkkam.ui.setting.reminder.model

import com.mulkkam.domain.model.reminder.ReminderSchedule

sealed interface ReminderUpdateUiState {
    data class Update(
        val reminderSchedule: ReminderSchedule,
    ) : ReminderUpdateUiState

    data object Add : ReminderUpdateUiState

    data object Idle : ReminderUpdateUiState
}
