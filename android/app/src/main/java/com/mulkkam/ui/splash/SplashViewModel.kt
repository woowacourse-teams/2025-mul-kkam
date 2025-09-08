package com.mulkkam.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.di.RepositoryInjection.membersRepository
import com.mulkkam.di.RepositoryInjection.onboardingRepository
import com.mulkkam.di.RepositoryInjection.tokenRepository
import com.mulkkam.domain.model.result.MulKkamError
import com.mulkkam.domain.model.result.toMulKkamError
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.model.UserAuthState
import kotlinx.coroutines.launch

class SplashViewModel : ViewModel() {
    private val _authUiState: MutableLiveData<MulKkamUiState<UserAuthState>> =
        MutableLiveData<MulKkamUiState<UserAuthState>>(MulKkamUiState.Idle)
    val authUiState: LiveData<MulKkamUiState<UserAuthState>> get() = _authUiState

    init {
        updateAuthState()
    }

    private fun updateAuthState() {
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
                onboardingRepository.getOnboardingCheck().getOrError()
            }.onSuccess { userAuthState ->
                _authUiState.value = MulKkamUiState.Success<UserAuthState>(userAuthState)
            }.onFailure {
                _authUiState.value = MulKkamUiState.Failure(it.toMulKkamError())
            }
        }
    }
}
