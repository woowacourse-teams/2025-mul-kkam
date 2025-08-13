package com.mulkkam.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.di.RepositoryInjection
import com.mulkkam.domain.model.result.toMulKkamError
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.model.UserAuthState
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val _authUiState: MutableLiveData<MulKkamUiState<UserAuthState>> =
        MutableLiveData<MulKkamUiState<UserAuthState>>(MulKkamUiState.Idle)
    val authUiState: LiveData<MulKkamUiState<UserAuthState>> get() = _authUiState

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
                RepositoryInjection.membersRepository.getMembersCheckOnboarding().getOrError()
            }.onSuccess { hasCompletedOnboarding ->
                val userAuthState =
                    when (hasCompletedOnboarding) {
                        true -> UserAuthState.ACTIVE_USER
                        false -> UserAuthState.UNONBOARDED
                    }
                _authUiState.value = MulKkamUiState.Success(userAuthState)
            }.onFailure {
                _authUiState.value = MulKkamUiState.Failure(it.toMulKkamError())
            }
        }
    }
}
