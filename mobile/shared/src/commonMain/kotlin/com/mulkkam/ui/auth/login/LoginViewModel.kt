package com.mulkkam.ui.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.domain.model.UserAuthState
import com.mulkkam.domain.model.result.toMulKkamError
import com.mulkkam.domain.repository.AuthRepository
import com.mulkkam.domain.repository.DevicesRepository
import com.mulkkam.domain.repository.TokenRepository
import com.mulkkam.domain.repository.VersionsRepository
import com.mulkkam.ui.model.MulKkamUiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val versionsRepository: VersionsRepository,
    private val devicesRepository: DevicesRepository,
    private val authRepository: AuthRepository,
    private val tokenRepository: TokenRepository,
) : ViewModel() {
    private val _authUiState: MutableStateFlow<MulKkamUiState<UserAuthState>> =
        MutableStateFlow(MulKkamUiState.Idle)
    val authUiState: StateFlow<MulKkamUiState<UserAuthState>> get() = _authUiState.asStateFlow()

    private val _isAppOutdated: MutableSharedFlow<Boolean> =
        MutableSharedFlow(replay = 0, extraBufferCapacity = 1)
    val isAppOutdated: SharedFlow<Boolean> get() = _isAppOutdated.asSharedFlow()

    private val numericPattern = Regex("""^\d+""")

    fun checkAppVersion(currentVersionName: String) {
        viewModelScope.launch {
            val minimumVersionResult: Result<String> =
                runCatching { versionsRepository.getMinimumVersion().getOrError() }
            val minimumVersion: String = minimumVersionResult.getOrNull() ?: return@launch
            _isAppOutdated.emit(isOutdated(currentVersionName, minimumVersion))
        }
    }

    private fun isOutdated(
        currentVersion: String,
        minimumVersion: String,
    ): Boolean {
        val currentParts = currentVersion.split(".").map { it.toNumericPart() }
        val minimumParts = minimumVersion.split(".").map { it.toNumericPart() }

        for (index in currentParts.indices) {
            val currentPart = currentParts[index]
            val minimumPart = minimumParts[index]

            if (currentPart < minimumPart) return true
            if (currentPart > minimumPart) return false
        }
        return false
    }

    private fun String.toNumericPart(): Int = numericPattern.find(this)?.value?.toIntOrNull() ?: 0

    fun loginWithKakao(token: String) {
        viewModelScope.launch {
            runCatching {
                _authUiState.value = MulKkamUiState.Loading
                val deviceUuid = devicesRepository.getDeviceUuid().getOrError()
                authRepository.postAuthKakao(token, deviceUuid).getOrError()
            }.onSuccess { authTokenInfo ->
                val accessToken = authTokenInfo.accessToken
                val refreshToken = authTokenInfo.refreshToken

                tokenRepository.saveAccessToken(accessToken)
                tokenRepository.saveRefreshToken(refreshToken)

                updateAuthStateWithOnboarding(authTokenInfo.onboardingCompleted)
            }.onFailure {
                _authUiState.value = MulKkamUiState.Failure(it.toMulKkamError())
            }
        }
    }

    fun loginWithApple(authorizationCode: String) {
        viewModelScope.launch {
            runCatching {
                _authUiState.value = MulKkamUiState.Loading
                val deviceUuid = devicesRepository.getDeviceUuid().getOrError()
                authRepository.postAuthApple(authorizationCode, deviceUuid).getOrError()
            }.onSuccess { authTokenInfo ->
                val accessToken = authTokenInfo.accessToken
                val refreshToken = authTokenInfo.refreshToken

                tokenRepository.saveAccessToken(accessToken)
                tokenRepository.saveRefreshToken(refreshToken)

                updateAuthStateWithOnboarding(authTokenInfo.onboardingCompleted)
            }.onFailure {
                _authUiState.value = MulKkamUiState.Failure(it.toMulKkamError())
            }
        }
    }

    private fun updateAuthStateWithOnboarding(onboardingCompleted: Boolean) {
        val userAuthState =
            when (onboardingCompleted) {
                true -> UserAuthState.ACTIVE_USER
                false -> UserAuthState.UNONBOARDED
            }

        _authUiState.value = MulKkamUiState.Success(userAuthState)
    }
}
