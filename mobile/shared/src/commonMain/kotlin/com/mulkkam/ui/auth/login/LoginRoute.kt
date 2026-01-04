package com.mulkkam.ui.auth.login
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import org.koin.compose.viewmodel.koinViewModel

@Composable
expect fun LoginRoute(
    padding: PaddingValues,
    onNavigateToOnboarding: () -> Unit,
    onNavigateToMain: () -> Unit,
    viewModel: LoginViewModel = koinViewModel(),
)
