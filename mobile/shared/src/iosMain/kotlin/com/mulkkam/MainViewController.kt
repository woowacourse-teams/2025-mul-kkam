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
    onRegisterPushNotification: (
        onTokenUpdated: (token: String) -> Unit,
        onPermissionUpdated: (isGranted: Boolean) -> Unit,
        onError: (errorMessage: String) -> Unit,
    ) -> Unit,
    onRequestMainPermissions: () -> Unit,
    appVersion: String,
) = ComposeUIViewController {
    MulKkamApp(
        onLogin = onLogin,
        onRegisterPushNotification = onRegisterPushNotification,
        onRequestMainPermissions = onRequestMainPermissions,
        appVersion = appVersion,
    )
}
