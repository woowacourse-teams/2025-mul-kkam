package com.mulkkam.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.di.RepositoryInjection
import com.mulkkam.ui.model.AppAuthState
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val _authState = MutableLiveData<AppAuthState>()
    val authState: LiveData<AppAuthState> = _authState

    fun loginWithKakao(token: String) {
        viewModelScope
            .launch {
                val result = RepositoryInjection.authRepository.postAuthKakao(token)
                runCatching {
                    val accessToken = result.getOrError()
                    RepositoryInjection.tokenRepository.saveAccessToken(accessToken)
                }.onFailure {
                    // TODO: 에러 처리
                }

                updateAuthStateWithOnboarding()
            }
    }

    suspend fun updateAuthStateWithOnboarding() {
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
