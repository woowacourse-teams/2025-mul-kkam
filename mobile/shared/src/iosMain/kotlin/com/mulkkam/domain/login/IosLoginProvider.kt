package com.mulkkam.domain.login

interface IosLoginProvider {
    fun loginWithKakao(
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit,
    )

// TODO: Apple 로그인 구현
//    fun loginWithApple()
}
