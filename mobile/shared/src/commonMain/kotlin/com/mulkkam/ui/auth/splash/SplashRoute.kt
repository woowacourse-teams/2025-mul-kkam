package com.mulkkam.ui.auth.splash

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable

@Composable
fun SplashRoute(
    padding: PaddingValues,
    onNavigateToLogin: () -> Unit,
    onNavigateToMain: () -> Unit,
) {
    SplashScreen(
        padding = padding,
        onNavigateToLogin = onNavigateToLogin,
        onNavigateToMain = onNavigateToMain,
    )
}
