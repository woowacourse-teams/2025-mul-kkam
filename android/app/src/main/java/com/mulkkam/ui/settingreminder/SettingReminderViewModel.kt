package com.mulkkam.ui.settingreminder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.di.RepositoryInjection.membersRepository
import com.mulkkam.di.RepositoryInjection.reminderRepository
import com.mulkkam.domain.model.reminder.ReminderSchedule
import com.mulkkam.domain.model.result.toMulKkamError
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.model.MulKkamUiState.Idle.toSuccessDataOrNull
import com.mulkkam.ui.settingreminder.model.ReminderUpdateUiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
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

    private val _reminderUpdateUiState: MutableStateFlow<ReminderUpdateUiState> =
        MutableStateFlow(ReminderUpdateUiState.Idle)
    val reminderUpdateUiState: StateFlow<ReminderUpdateUiState> =
        _reminderUpdateUiState.asStateFlow()

    private val _onReminderUpdated: MutableSharedFlow<MulKkamUiState<Unit>> =
        MutableSharedFlow<MulKkamUiState<Unit>>()
    val onReminderUpdated: SharedFlow<MulKkamUiState<Unit>> = _onReminderUpdated

    init {
        loadReminderSchedules()
    }

    private fun loadReminderSchedules() {
        viewModelScope.launch {
            runCatching {
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
            val isEnabled = isReminderEnabled.value.toSuccessDataOrNull() ?: return@launch
            runCatching {
                _isReminderEnabled.value = MulKkamUiState.Success(!isEnabled)
                membersRepository.patchMembersReminder(!isEnabled).getOrError()
            }.onSuccess {
                loadReminderSchedules()
            }
        }
    }

    fun updateReminderUpdateUiState(reminderUpdateUiState: ReminderUpdateUiState) {
        _reminderUpdateUiState.value = reminderUpdateUiState
    }

    fun handleReminderUpdateAction(selectedTime: LocalTime) {
        val currentMode = reminderUpdateUiState.value
        when (currentMode) {
            is ReminderUpdateUiState.Update -> {
                updateReminder(currentMode.reminderSchedule.copy(schedule = selectedTime))
            }

            is ReminderUpdateUiState.Add -> {
                addReminder(selectedTime)
            }

            is ReminderUpdateUiState.Idle -> Unit
        }
        updateReminderUpdateUiState(ReminderUpdateUiState.Idle)
    }

    private fun addReminder(time: LocalTime) {
        viewModelScope.launch {
            runCatching {
                reminderRepository.postReminder(time).getOrError()
            }.onSuccess {
                _onReminderUpdated.emit(MulKkamUiState.Success(Unit))
                loadReminderSchedules()
            }.onFailure {
                _onReminderUpdated.emit(MulKkamUiState.Failure(it.toMulKkamError()))
            }
        }
    }

    private fun updateReminder(reminderSchedule: ReminderSchedule) {
        viewModelScope.launch {
            runCatching {
                reminderRepository.patchReminder(reminderSchedule).getOrError()
            }.onSuccess {
                _onReminderUpdated.emit(MulKkamUiState.Success(Unit))
                loadReminderSchedules()
            }.onFailure {
                _onReminderUpdated.emit(MulKkamUiState.Failure(it.toMulKkamError()))
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
