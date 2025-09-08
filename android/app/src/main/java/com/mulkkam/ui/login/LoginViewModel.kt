package com.mulkkam.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.di.RepositoryInjection
import com.mulkkam.di.RepositoryInjection.onboardingRepository
import com.mulkkam.di.RepositoryInjection.versionsRepository
import com.mulkkam.domain.model.result.toMulKkamError
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.model.UserAuthState
import com.mulkkam.ui.util.MutableSingleLiveData
import com.mulkkam.ui.util.SingleLiveData
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
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
                RepositoryInjection.authRepository.postAuthKakao(token).getOrError()
            }.onSuccess { tokens ->
                val accessToken = tokens.accessToken
                val refreshToken = tokens.refreshToken

                RepositoryInjection.tokenRepository.saveAccessToken(accessToken)
                RepositoryInjection.tokenRepository.saveRefreshToken(refreshToken)

                updateAuthStateWithOnboarding()
            }.onFailure {
                _authUiState.value = MulKkamUiState.Failure(it.toMulKkamError())
            }
        }
    }

    private fun updateAuthStateWithOnboarding() {
        viewModelScope.launch {
            runCatching {
                onboardingRepository.getOnboardingCheck().getOrError()
            }.onSuccess { userAuthState ->
                _authUiState.value = MulKkamUiState.Success(userAuthState)
            }.onFailure {
                _authUiState.value = MulKkamUiState.Failure(it.toMulKkamError())
            }
        }
    }
}
