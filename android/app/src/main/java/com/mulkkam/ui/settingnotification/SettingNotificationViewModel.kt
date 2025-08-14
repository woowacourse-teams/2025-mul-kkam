package com.mulkkam.ui.settingnotification

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.di.RepositoryInjection.membersRepository
import com.mulkkam.domain.model.members.NotificationAgreedInfo
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.model.MulKkamUiState.Idle.toSuccessDataOrNull
import com.mulkkam.ui.util.MutableSingleLiveData
import com.mulkkam.ui.util.SingleLiveData
import kotlinx.coroutines.launch

class SettingNotificationViewModel : ViewModel() {
    private val _settingsUiState: MutableLiveData<MulKkamUiState<NotificationAgreedInfo>> =
        MutableLiveData(MulKkamUiState.Idle)
    val settingsUiState: LiveData<MulKkamUiState<NotificationAgreedInfo>> get() = _settingsUiState

    private val _onError: MutableSingleLiveData<Unit> = MutableSingleLiveData()
    val onError: SingleLiveData<Unit> get() = _onError

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            runCatching {
                membersRepository.getMembersNotificationSettings().getOrError()
            }.onSuccess {
                _settingsUiState.value = MulKkamUiState.Success<NotificationAgreedInfo>(it)
            }.onFailure {
                _onError.setValue(Unit)
            }
        }
    }

    fun updateNightNotification(agreed: Boolean) {
        if (settingsUiState.value is MulKkamUiState.Loading) return
        val current = settingsUiState.value?.toSuccessDataOrNull() ?: return

        viewModelScope.launch {
            runCatching {
                _settingsUiState.value = MulKkamUiState.Loading
                membersRepository.patchMembersNotificationNight(agreed).getOrError()
            }.onSuccess {
                _settingsUiState.value = MulKkamUiState.Success(current.copy(isNightNotificationAgreed = agreed))
            }.onFailure {
                _settingsUiState.value = MulKkamUiState.Success(current.copy(isNightNotificationAgreed = agreed.not()))
                _onError.setValue(Unit)
            }
        }
    }

    fun updateMarketingNotification(agreed: Boolean) {
        if (settingsUiState.value is MulKkamUiState.Loading) return
        val current = settingsUiState.value?.toSuccessDataOrNull() ?: return

        viewModelScope.launch {
            runCatching {
                _settingsUiState.value = MulKkamUiState.Loading
                membersRepository.patchMembersNotificationMarketing(agreed).getOrError()
            }.onSuccess {
                _settingsUiState.value = MulKkamUiState.Success(current.copy(isMarketingNotificationAgreed = agreed))
            }.onFailure {
                _settingsUiState.value = MulKkamUiState.Success(current.copy(isMarketingNotificationAgreed = agreed.not()))
                _onError.setValue(Unit)
            }
        }
    }
}
