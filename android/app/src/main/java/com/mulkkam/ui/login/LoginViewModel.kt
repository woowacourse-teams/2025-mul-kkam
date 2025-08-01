package com.mulkkam.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.di.RepositoryInjection
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    fun loginWithKakao(token: String) {
        viewModelScope.launch {
            val accessToken = RepositoryInjection.authRepository.postAuthKakao(token)
            RepositoryInjection.tokenRepository.saveAccessToken(accessToken)
        }
    }
}
