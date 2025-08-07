package com.mulkkam.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mulkkam.di.RepositoryInjection
import com.mulkkam.ui.model.AppAuthState

class SplashViewModel : ViewModel() {
    private val _authState = MutableLiveData<AppAuthState>()
    val authState: LiveData<AppAuthState> = _authState

    fun updateEntryState() {
        val token = RepositoryInjection.tokenRepository.getAccessToken()
        if (token == null) {
            _authState.value = AppAuthState.UNAUTHORIZED
            return
        }

        // TODO: 온보딩 여부 API 연결
        val hasCompletedOnboarding = false
        _authState.value =
            when {
                hasCompletedOnboarding -> AppAuthState.ACTIVE_USER
                else -> AppAuthState.UNONBOARDED
            }
    }
}
