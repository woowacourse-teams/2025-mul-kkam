package com.mulkkam.ui.settingreminder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.di.RepositoryInjection.reminderRepository
import com.mulkkam.domain.model.reminder.ReminderSchedule
import com.mulkkam.domain.model.result.toMulKkamError
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.model.MulKkamUiState.Idle.toSuccessDataOrNull
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalTime

class SettingReminderViewModel : ViewModel() {
    private val _isReminderEnabled: MutableStateFlow<MulKkamUiState<Boolean>> =
        MutableStateFlow(MulKkamUiState.Idle)
    val isReminderEnabled: StateFlow<MulKkamUiState<Boolean>> = _isReminderEnabled.asStateFlow()

    private val _reminderSchedules: MutableStateFlow<MulKkamUiState<List<ReminderSchedule>>> =
        MutableStateFlow(MulKkamUiState.Idle)
    val reminderSchedules: StateFlow<MulKkamUiState<List<ReminderSchedule>>> =
        _reminderSchedules.asStateFlow()

    init {
        loadReminderSchedules()
    }

    private fun loadReminderSchedules() {
        if (isReminderEnabled.value == MulKkamUiState.Loading) return
        viewModelScope.launch {
            runCatching {
                _isReminderEnabled.value = MulKkamUiState.Loading
                reminderRepository.getReminder().getOrError()
            }.onSuccess { reminderConfig ->
                _isReminderEnabled.value = MulKkamUiState.Success(reminderConfig.isReminderEnabled)
                _reminderSchedules.value =
                    MulKkamUiState.Success(reminderConfig.reminderSchedules ?: emptyList())
            }.onFailure {
                _isReminderEnabled.value = MulKkamUiState.Failure(it.toMulKkamError())
            }
        }
    }

    fun updateReminderEnabled() {
        viewModelScope.launch {
            runCatching {
                val asdf = _isReminderEnabled.value.toSuccessDataOrNull() ?: return@launch
                _isReminderEnabled.value = MulKkamUiState.Success(!asdf)
            }
        }
    }

    fun addReminder(time: LocalTime) {
        viewModelScope.launch {
            runCatching {
                reminderRepository.postReminder(time).getOrError()
            }.onSuccess {
                loadReminderSchedules()
            }
        }
    }

    fun updateReminder(reminderSchedule: ReminderSchedule) {
        viewModelScope.launch {
            runCatching {
                reminderRepository.patchReminder(reminderSchedule).getOrError()
            }.onSuccess {
                loadReminderSchedules()
            }
        }
    }

    fun removeReminder(id: Long) {
        viewModelScope.launch {
            runCatching {
                reminderRepository.deleteReminder(id).getOrError()
            }.onSuccess {
                loadReminderSchedules()
            }
        }
    }
}
