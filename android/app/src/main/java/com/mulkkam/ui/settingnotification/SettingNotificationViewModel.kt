package com.mulkkam.ui.settingnotification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.domain.logger.Logger
import com.mulkkam.domain.model.logger.LogEvent
import com.mulkkam.domain.model.members.NotificationAgreedInfo
import com.mulkkam.domain.repository.MembersRepository
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.settingnotification.model.SettingNotificationEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingNotificationViewModel
    @Inject
    constructor(
        private val membersRepository: MembersRepository,
        private val logger: Logger,
    ) : ViewModel() {
        private val _settingsUiState: MutableStateFlow<MulKkamUiState<NotificationAgreedInfo>> =
            MutableStateFlow(MulKkamUiState.Idle)
        val settingsUiState: StateFlow<MulKkamUiState<NotificationAgreedInfo>> get() = _settingsUiState.asStateFlow()

        private val _notificationEvents: MutableSharedFlow<SettingNotificationEvent> = MutableSharedFlow()
        val notificationEvents: SharedFlow<SettingNotificationEvent> get() = _notificationEvents.asSharedFlow()

        init {
            loadSettings()
        }

        private fun loadSettings() {
            viewModelScope.launch {
                runCatching {
                    _settingsUiState.value = MulKkamUiState.Loading
                    membersRepository.getMembersNotificationSettings().getOrError()
                }.onSuccess { settings ->
                    _settingsUiState.value = MulKkamUiState.Success(settings)
                }.onFailure {
                    _settingsUiState.value = MulKkamUiState.Idle
                    _notificationEvents.emit(SettingNotificationEvent.Error)
                }
            }
        }

        fun updateNightNotification(agreed: Boolean) {
            if (settingsUiState.value is MulKkamUiState.Loading) return
            val current: NotificationAgreedInfo = (settingsUiState.value as? MulKkamUiState.Success)?.data ?: return

            viewModelScope.launch {
                runCatching {
                    logger.info(
                        LogEvent.PUSH_NOTIFICATION,
                        "Night notification preference updated to $agreed",
                    )
                    _settingsUiState.value = MulKkamUiState.Loading
                    membersRepository.patchMembersNotificationNight(agreed).getOrError()
                }.onSuccess {
                    _settingsUiState.value =
                        MulKkamUiState.Success(current.copy(isNightNotificationAgreed = agreed))
                    _notificationEvents.emit(SettingNotificationEvent.NightUpdated(agreed))
                }.onFailure {
                    _settingsUiState.value =
                        MulKkamUiState.Success(current.copy(isNightNotificationAgreed = !agreed))
                    _notificationEvents.emit(SettingNotificationEvent.Error)
                }
            }
        }

        fun updateMarketingNotification(agreed: Boolean) {
            if (settingsUiState.value is MulKkamUiState.Loading) return
            val current: NotificationAgreedInfo = (settingsUiState.value as? MulKkamUiState.Success)?.data ?: return

            viewModelScope.launch {
                runCatching {
                    logger.info(
                        LogEvent.PUSH_NOTIFICATION,
                        "Marketing notification preference updated to $agreed",
                    )
                    _settingsUiState.value = MulKkamUiState.Loading
                    membersRepository.patchMembersNotificationMarketing(agreed).getOrError()
                }.onSuccess {
                    _settingsUiState.value =
                        MulKkamUiState.Success(current.copy(isMarketingNotificationAgreed = agreed))
                    _notificationEvents.emit(SettingNotificationEvent.MarketingUpdated(agreed))
                }.onFailure {
                    _settingsUiState.value =
                        MulKkamUiState.Success(current.copy(isMarketingNotificationAgreed = !agreed))
                    _notificationEvents.emit(SettingNotificationEvent.Error)
                }
            }
        }
    }
