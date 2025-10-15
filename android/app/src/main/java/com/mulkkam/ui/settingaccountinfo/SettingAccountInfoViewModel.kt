package com.mulkkam.ui.settingaccountinfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.R
import com.mulkkam.di.LoggingInjection.mulKkamLogger
import com.mulkkam.di.RepositoryInjection.authRepository
import com.mulkkam.di.RepositoryInjection.membersRepository
import com.mulkkam.di.RepositoryInjection.tokenRepository
import com.mulkkam.domain.model.logger.LogEvent
import com.mulkkam.ui.util.MutableSingleLiveData
import com.mulkkam.ui.util.SingleLiveData
import kotlinx.coroutines.launch

class SettingAccountInfoViewModel : ViewModel() {
    private val _accountInfo: MutableLiveData<List<SettingAccountUiModel>> = MutableLiveData()
    val accountInfo: LiveData<List<SettingAccountUiModel>> = _accountInfo

    private val _onDeleteAccount: MutableSingleLiveData<Unit> = MutableSingleLiveData()
    val onDeleteAccount: SingleLiveData<Unit> = _onDeleteAccount

    private val _onLogout: MutableSingleLiveData<Unit> = MutableSingleLiveData()
    val onLogout: SingleLiveData<Unit> = _onLogout

    init {
        _accountInfo.value = accountInfoList
    }

    fun deleteAccount() {
        viewModelScope.launch {
            runCatching {
                mulKkamLogger.warn(
                    LogEvent.USER_ACTION,
                    "User initiated account deletion",
                )
                membersRepository.deleteMembers().getOrError()
            }.onSuccess {
                deleteTokens()
                _onDeleteAccount.setValue(Unit)
            }.onFailure {
                // TODO: 에러 처리
            }
        }
    }

    fun logoutAccount() {
        viewModelScope.launch {
            runCatching {
                mulKkamLogger.info(
                    LogEvent.USER_ACTION,
                    "User requested logout",
                )
                authRepository.postAuthLogout().getOrError()
            }.onSuccess {
                deleteTokens()
                _onLogout.setValue(Unit)
            }.onFailure {
                // TODO: 에러 처리
            }
        }
    }

    private suspend fun deleteTokens() {
        runCatching {
            tokenRepository.deleteAccessToken().getOrError()
            tokenRepository.deleteFcmToken().getOrError()
            tokenRepository.deleteRefreshToken().getOrError()
        }
    }

    companion object {
        val accountInfoList =
            listOf(
                SettingAccountUiModel(R.string.setting_account_info_logout),
                SettingAccountUiModel(R.string.setting_account_info_delete_account),
            )
    }
}
