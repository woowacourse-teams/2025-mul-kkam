package com.mulkkam.domain.login

interface IosLoginProvider {
    fun loginWithKakao(
        onSuccess: (token: String) -> Unit,
        onFailure: (errorMessage: String) -> Unit,
    )

// TODO: Apple 로그인 구현
//    fun loginWithApple()
}
