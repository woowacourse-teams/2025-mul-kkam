package com.mulkkam.ui.settingreminder.model

import com.mulkkam.domain.model.reminder.ReminderSchedule

sealed interface ReminderUpdateUiState {
    data class Update(
        val reminderSchedule: ReminderSchedule,
    ) : ReminderUpdateUiState

    data object Add : ReminderUpdateUiState
}
