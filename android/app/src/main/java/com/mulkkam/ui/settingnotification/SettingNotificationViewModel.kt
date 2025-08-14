package com.mulkkam.ui.settingnotification

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.di.RepositoryInjection.membersRepository
import com.mulkkam.domain.model.members.NotificationAgreedInfo
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.util.MutableSingleLiveData
import com.mulkkam.ui.util.SingleLiveData
import kotlinx.coroutines.launch

class SettingNotificationViewModel : ViewModel() {
    private val _settingsUiState = MutableLiveData<MulKkamUiState<NotificationAgreedInfo>>(MulKkamUiState.Idle)
    val settingsUiState: LiveData<MulKkamUiState<NotificationAgreedInfo>> get() = _settingsUiState

    private val _onError = MutableSingleLiveData<Unit>()
    val onError: SingleLiveData<Unit> get() = _onError

    private val _onMarketingUpdated = MutableSingleLiveData<Boolean>()
    val onMarketingUpdated: SingleLiveData<Boolean> get() = _onMarketingUpdated

    private val _onNightUpdated = MutableSingleLiveData<Boolean>()
    val onNightUpdated: SingleLiveData<Boolean> get() = _onNightUpdated

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            runCatching {
                membersRepository.getMembersNotificationSettings().getOrError()
            }.onSuccess {
                _settingsUiState.value = MulKkamUiState.Success(it)
            }.onFailure {
                _onError.setValue(Unit)
            }
        }
    }

    fun updateNightNotification(agreed: Boolean) {
        if (settingsUiState.value is MulKkamUiState.Loading) return
        val current = (settingsUiState.value as? MulKkamUiState.Success)?.data ?: return

        viewModelScope.launch {
            runCatching {
                _settingsUiState.value = MulKkamUiState.Loading
                membersRepository.patchMembersNotificationNight(agreed).getOrError()
            }.onSuccess {
                _settingsUiState.value = MulKkamUiState.Success(current.copy(isNightNotificationAgreed = agreed))
                _onNightUpdated.setValue(agreed)
            }.onFailure {
                _settingsUiState.value = MulKkamUiState.Success(current.copy(isNightNotificationAgreed = !agreed))
                _onError.setValue(Unit)
            }
        }
    }

    fun updateMarketingNotification(agreed: Boolean) {
        if (settingsUiState.value is MulKkamUiState.Loading) return
        val current = (settingsUiState.value as? MulKkamUiState.Success)?.data ?: return

        viewModelScope.launch {
            runCatching {
                _settingsUiState.value = MulKkamUiState.Loading
                membersRepository.patchMembersNotificationMarketing(agreed).getOrError()
            }.onSuccess {
                _settingsUiState.value = MulKkamUiState.Success(current.copy(isMarketingNotificationAgreed = agreed))
                _onMarketingUpdated.setValue(agreed)
            }.onFailure {
                _settingsUiState.value = MulKkamUiState.Success(current.copy(isMarketingNotificationAgreed = !agreed))
                _onError.setValue(Unit)
            }
        }
    }
}
