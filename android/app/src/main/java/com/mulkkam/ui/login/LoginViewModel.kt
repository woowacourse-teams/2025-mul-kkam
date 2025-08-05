package com.mulkkam.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.di.RepositoryInjection
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    fun loginWithKakao(token: String) {
        viewModelScope.launch {
            // TODO: 서버와의 통신 실패 시 에러 처리 구현 필요
            val result = RepositoryInjection.authRepository.postAuthKakao(token)
            if (!result.isSuccess) {
                // TODO: 에러 처리
            }
            val accessToken = result.data!!
            RepositoryInjection.tokenRepository.saveAccessToken(accessToken)
        }
    }
}
