package com.mulkkam.ui.setting.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.domain.logger.Logger
import com.mulkkam.domain.model.logger.LogEvent
import com.mulkkam.domain.repository.MembersRepository
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.model.toSuccessDataOrNull
import com.mulkkam.ui.setting.notification.model.SettingNotificationEvent
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingNotificationViewModel(
    private val membersRepository: MembersRepository,
    private val logger: Logger,
) : ViewModel() {
    private val _marketingNotificationState: MutableStateFlow<MulKkamUiState<Boolean>> =
        MutableStateFlow(MulKkamUiState.Idle)
    val marketingNotificationState: StateFlow<MulKkamUiState<Boolean>> get() = _marketingNotificationState.asStateFlow()

    private val _nightNotificationState: MutableStateFlow<MulKkamUiState<Boolean>> =
        MutableStateFlow(MulKkamUiState.Idle)
    val nightNotificationState: StateFlow<MulKkamUiState<Boolean>> get() = _nightNotificationState.asStateFlow()

    private val _notificationEvents: MutableSharedFlow<SettingNotificationEvent> =
        MutableSharedFlow()
    val notificationEvents: SharedFlow<SettingNotificationEvent> get() = _notificationEvents.asSharedFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            runCatching {
                _marketingNotificationState.value = MulKkamUiState.Loading
                _nightNotificationState.value = MulKkamUiState.Loading
                membersRepository.getMembersNotificationSettings().getOrError()
            }.onSuccess { settings ->
                _marketingNotificationState.value = MulKkamUiState.Success(settings.isMarketingNotificationAgreed)
                _nightNotificationState.value = MulKkamUiState.Success(settings.isNightNotificationAgreed)
            }.onFailure {
                _marketingNotificationState.value = MulKkamUiState.Idle
                _nightNotificationState.value = MulKkamUiState.Idle
                _notificationEvents.emit(SettingNotificationEvent.Error)
            }
        }
    }

    fun updateNightNotification(agreed: Boolean) {
        val previousValue: Boolean = nightNotificationState.value.toSuccessDataOrNull() ?: return

        _nightNotificationState.value = MulKkamUiState.Success(agreed)
        viewModelScope.launch {
            runCatching {
                logger.info(
                    LogEvent.PUSH_NOTIFICATION,
                    "Night notification preference updated to $agreed",
                )
                membersRepository.patchMembersNotificationNight(agreed).getOrError()
            }.onSuccess {
                _notificationEvents.emit(SettingNotificationEvent.NightUpdated(agreed))
            }.onFailure { throwable ->
                if (throwable is CancellationException) return@onFailure
                if (_nightNotificationState.value.toSuccessDataOrNull() == agreed) {
                    _nightNotificationState.value = MulKkamUiState.Success(previousValue)
                }
                _notificationEvents.emit(SettingNotificationEvent.Error)
            }
        }
    }

    fun updateMarketingNotification(agreed: Boolean) {
        val previousValue: Boolean = marketingNotificationState.value.toSuccessDataOrNull() ?: return

        _marketingNotificationState.value = MulKkamUiState.Success(agreed)
        viewModelScope.launch {
            runCatching {
                logger.info(
                    LogEvent.PUSH_NOTIFICATION,
                    "Marketing notification preference updated to $agreed",
                )
                membersRepository.patchMembersNotificationMarketing(agreed).getOrError()
            }.onSuccess {
                _notificationEvents.emit(SettingNotificationEvent.MarketingUpdated(agreed))
            }.onFailure { throwable ->
                if (throwable is CancellationException) return@onFailure
                if (_marketingNotificationState.value.toSuccessDataOrNull() == agreed) {
                    _marketingNotificationState.value = MulKkamUiState.Success(previousValue)
                }
                _notificationEvents.emit(SettingNotificationEvent.Error)
            }
        }
    }
}
