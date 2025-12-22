package com.mulkkam.ui.auth.login

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable

@Composable
fun LoginRoute(
    padding: PaddingValues,
    onNavigateToOnboarding: () -> Unit,
) {
    LoginScreen(
        padding = padding,
        onNavigateToOnboarding = onNavigateToOnboarding,
    )
}
