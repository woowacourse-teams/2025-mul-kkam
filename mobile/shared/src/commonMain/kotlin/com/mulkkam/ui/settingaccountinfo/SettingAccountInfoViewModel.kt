package com.mulkkam.ui.settingaccountinfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.domain.logger.Logger
import com.mulkkam.domain.model.logger.LogEvent
import com.mulkkam.domain.repository.AuthRepository
import com.mulkkam.domain.repository.MembersRepository
import com.mulkkam.domain.repository.TokenRepository
import com.mulkkam.ui.settingaccountinfo.model.AccountInfoType
import com.mulkkam.ui.settingaccountinfo.model.SettingAccountInfoEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingAccountInfoViewModel(
    private val membersRepository: MembersRepository,
    private val tokenRepository: TokenRepository,
    private val authRepository: AuthRepository,
    private val logger: Logger,
) : ViewModel() {
    private val _accountInfo: MutableStateFlow<List<AccountInfoType>> =
        MutableStateFlow(AccountInfoType.entries)
    val accountInfo: StateFlow<List<AccountInfoType>> = _accountInfo.asStateFlow()

    private val _settingAccountInfoEvent: MutableSharedFlow<SettingAccountInfoEvent> =
        MutableSharedFlow()
    val settingAccountInfoEvent: SharedFlow<SettingAccountInfoEvent> =
        _settingAccountInfoEvent.asSharedFlow()

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
                _settingAccountInfoEvent.emit(SettingAccountInfoEvent.DeleteSuccess)
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
                _settingAccountInfoEvent.emit(SettingAccountInfoEvent.LogoutSuccess)
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
}
