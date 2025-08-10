package com.mulkkam.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.di.RepositoryInjection.membersRepository
import com.mulkkam.di.RepositoryInjection.tokenRepository
import com.mulkkam.ui.model.AppAuthState
import kotlinx.coroutines.launch

class SplashViewModel : ViewModel() {
    private val _authState = MutableLiveData<AppAuthState>()
    val authState: LiveData<AppAuthState> = _authState

    fun updateAuthState() {
        viewModelScope.launch {
            runCatching {
                val token = tokenRepository.getAccessToken().getOrError()
                if (token == null) {
                    _authState.value = AppAuthState.UNAUTHORIZED
                    return@launch
                }
            }.onFailure {
                _authState.value = AppAuthState.UNAUTHORIZED
                return@launch
            }
            updateAuthStateWithOnboarding()
        }
    }

    private suspend fun updateAuthStateWithOnboarding() {
        runCatching {
            membersRepository.getMembersCheckOnboarding().getOrError()
        }.onSuccess { hasCompletedOnboarding ->
            _authState.value =
                when {
                    hasCompletedOnboarding -> AppAuthState.ACTIVE_USER
                    else -> AppAuthState.UNONBOARDED
                }
        }.onFailure {
            _authState.value = AppAuthState.UNAUTHORIZED
        }
    }
}
