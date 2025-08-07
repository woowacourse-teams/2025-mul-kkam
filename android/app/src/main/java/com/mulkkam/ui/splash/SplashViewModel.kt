package com.mulkkam.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.di.RepositoryInjection
import com.mulkkam.ui.model.AppAuthState
import kotlinx.coroutines.launch

class SplashViewModel : ViewModel() {
    private val _authState = MutableLiveData<AppAuthState>()
    val authState: LiveData<AppAuthState> = _authState

    fun updateAuthState() {
        viewModelScope.launch {
            runCatching {
                val result = RepositoryInjection.tokenRepository.getAccessToken()
                result.getOrError()
            }.onSuccess { token ->
                if (token == null) {
                    _authState.value = AppAuthState.UNAUTHORIZED
                    return@onSuccess
                }
            }
        }

        updateAuthStateWithOnboarding()
    }

    private fun updateAuthStateWithOnboarding() {
        viewModelScope.launch {
            val result = RepositoryInjection.membersRepository.getMembersCheckOnboarding()
            runCatching {
                val hasCompletedOnboarding = result.getOrError()
                _authState.value =
                    when {
                        hasCompletedOnboarding -> AppAuthState.ACTIVE_USER
                        else -> AppAuthState.UNONBOARDED
                    }
            }.onFailure {
                // TODO: 에러 처리
            }
        }
    }
}
