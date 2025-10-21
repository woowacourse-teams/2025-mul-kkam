package com.mulkkam.ui.settingaccountinfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.R
import com.mulkkam.domain.logger.Logger
import com.mulkkam.domain.model.logger.LogEvent
import com.mulkkam.domain.repository.AuthRepository
import com.mulkkam.domain.repository.MembersRepository
import com.mulkkam.domain.repository.TokenRepository
import com.mulkkam.ui.util.MutableSingleLiveData
import com.mulkkam.ui.util.SingleLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingAccountInfoViewModel
    @Inject
    constructor(
        private val membersRepository: MembersRepository,
        private val tokenRepository: TokenRepository,
        private val authRepository: AuthRepository,
        private val logger: Logger,
    ) : ViewModel() {
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
                    logger.warn(
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
                    logger.info(
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
