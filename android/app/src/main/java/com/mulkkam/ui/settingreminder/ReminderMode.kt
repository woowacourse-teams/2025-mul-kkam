package com.mulkkam.ui.settingreminder

import com.mulkkam.domain.model.reminder.ReminderSchedule

sealed interface ReminderMode {
    data class Update(
        val reminderSchedule: ReminderSchedule,
    ) : ReminderMode

    data object Add : ReminderMode
}
