package com.mulkkam.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mulkkam.di.RepositoryInjection
import com.mulkkam.ui.model.AppEntryState

class SplashViewModel : ViewModel() {
    private val _entryState = MutableLiveData<AppEntryState>()
    val entryState: LiveData<AppEntryState> = _entryState

    fun updateEntryState() {
        val token = RepositoryInjection.tokenRepository.getAccessToken()
        if (token == null) {
            _entryState.value = AppEntryState.UNAUTHENTICATED
            return
        }

        // TODO: 온보딩 여부 API 연결
        val hasCompletedOnboarding = false
        _entryState.value =
            when {
                hasCompletedOnboarding -> AppEntryState.ACTIVE_USER
                else -> AppEntryState.UNONBOARDED
            }
    }
}
