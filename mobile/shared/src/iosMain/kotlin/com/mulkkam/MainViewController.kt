package com.mulkkam

import androidx.compose.ui.window.ComposeUIViewController
import com.mulkkam.ui.auth.login.model.AuthPlatform

@Suppress("FunctionName")
fun MainViewController(
    onLogin: (
        authPlatform: AuthPlatform,
        onSuccess: (token: String) -> Unit,
        onError: (errorMessage: String) -> Unit,
    ) -> Unit,
    appVersion: String,
) = ComposeUIViewController { MulKkamApp(onLogin, appVersion) }
