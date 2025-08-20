package com.mulkkam.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.di.RepositoryInjection.membersRepository
import com.mulkkam.di.RepositoryInjection.tokenRepository
import com.mulkkam.di.RepositoryInjection.versionsRepository
import com.mulkkam.domain.model.result.MulKkamError
import com.mulkkam.domain.model.result.toMulKkamError
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.model.UserAuthState
import com.mulkkam.ui.util.MutableSingleLiveData
import com.mulkkam.ui.util.SingleLiveData
import kotlinx.coroutines.launch

class SplashViewModel : ViewModel() {
    private val _authUiState: MutableLiveData<MulKkamUiState<UserAuthState>> =
        MutableLiveData<MulKkamUiState<UserAuthState>>(MulKkamUiState.Idle)
    val authUiState: LiveData<MulKkamUiState<UserAuthState>> get() = _authUiState

    private val _isAppOutdated: MutableSingleLiveData<Boolean> = MutableSingleLiveData()
    val isAppOutdated: SingleLiveData<Boolean> get() = _isAppOutdated

    fun checkAppVersion(currentVersionName: String) {
        viewModelScope.launch {
            runCatching {
                versionsRepository.getMinimumVersion().getOrError()
            }.onSuccess { minimumVersion ->
                _isAppOutdated.setValue(isOutdated(currentVersionName, minimumVersion))
            }.onFailure {
                updateAuthState()
                _isAppOutdated.setValue(true)
            }
        }
    }

    private fun isOutdated(
        currentVersion: String,
        minimumVersion: String,
    ): Boolean {
        val currentParts: List<Int> = currentVersion.split(".").mapNotNull { it.toIntOrNull() }
        val minimumParts: List<Int> = minimumVersion.split(".").mapNotNull { it.toIntOrNull() }
        val maxLength: Int = maxOf(currentParts.size, minimumParts.size)

        for (index in 0 until maxLength) {
            val currentPart: Int = currentParts.getOrElse(index) { 0 }
            val minimumPart: Int = minimumParts.getOrElse(index) { 0 }

            when {
                currentPart < minimumPart -> return true
                currentPart > minimumPart -> return false
            }
        }
        return false
    }

    fun updateAuthState() {
        if (_authUiState.value is MulKkamUiState.Loading) return
        viewModelScope.launch {
            runCatching {
                _authUiState.value = MulKkamUiState.Loading
                tokenRepository.getAccessToken().getOrError()
            }.onSuccess { accessToken ->
                when (accessToken.isNullOrBlank().not()) {
                    true -> updateAuthStateWithOnboarding()
                    false -> _authUiState.value = MulKkamUiState.Failure(MulKkamError.AccountError.InvalidToken)
                }
            }.onFailure {
                _authUiState.value = MulKkamUiState.Failure(it.toMulKkamError())
            }
        }
    }

    private fun updateAuthStateWithOnboarding() {
        viewModelScope.launch {
            runCatching {
                membersRepository.getMembersCheckOnboarding().getOrError()
            }.onSuccess { userAuthState ->
                _authUiState.value = MulKkamUiState.Success<UserAuthState>(userAuthState)
            }.onFailure {
                _authUiState.value = MulKkamUiState.Failure(it.toMulKkamError())
            }
        }
    }
}
