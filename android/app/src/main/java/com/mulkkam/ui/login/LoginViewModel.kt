package com.mulkkam.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.domain.model.result.toMulKkamError
import com.mulkkam.domain.repository.AuthRepository
import com.mulkkam.domain.repository.DevicesRepository
import com.mulkkam.domain.repository.TokenRepository
import com.mulkkam.domain.repository.VersionsRepository
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.model.UserAuthState
import com.mulkkam.ui.util.MutableSingleLiveData
import com.mulkkam.ui.util.SingleLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel
    @Inject
    constructor(
        private val versionsRepository: VersionsRepository,
        private val devicesRepository: DevicesRepository,
        private val authRepository: AuthRepository,
        private val tokenRepository: TokenRepository,
    ) : ViewModel() {
        private val _authUiState: MutableLiveData<MulKkamUiState<UserAuthState>> =
            MutableLiveData<MulKkamUiState<UserAuthState>>(MulKkamUiState.Idle)
        val authUiState: LiveData<MulKkamUiState<UserAuthState>> get() = _authUiState

        private val _isAppOutdated: MutableSingleLiveData<Boolean> = MutableSingleLiveData()
        val isAppOutdated: SingleLiveData<Boolean> get() = _isAppOutdated

        private val numericPattern = Regex("""^\d+""")

        fun checkAppVersion(currentVersionName: String) {
            viewModelScope.launch {
                runCatching {
                    versionsRepository.getMinimumVersion().getOrError()
                }.onSuccess { minimumVersion ->
                    _isAppOutdated.setValue(isOutdated(currentVersionName, minimumVersion))
                }
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

        private fun updateAuthStateWithOnboarding(onboardingCompleted: Boolean) {
            val userAuthState =
                when (onboardingCompleted) {
                    true -> UserAuthState.ACTIVE_USER
                    false -> UserAuthState.UNONBOARDED
                }

            _authUiState.value = MulKkamUiState.Success(userAuthState)
        }
    }
