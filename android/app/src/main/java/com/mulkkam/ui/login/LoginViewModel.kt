package com.mulkkam.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.di.RepositoryInjection
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    fun loginWithKakao(token: String) {
        viewModelScope.launch {
            val result = RepositoryInjection.authRepository.postAuthKakao(token)
            runCatching {
                val accessToken = result.getOrError()
                RepositoryInjection.tokenRepository.saveAccessToken(accessToken)
            }.onFailure {
                // TODO: 에러 처리
            }
        }
    }
}
