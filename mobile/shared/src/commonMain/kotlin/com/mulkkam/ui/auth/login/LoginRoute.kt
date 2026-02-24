package com.mulkkam.ui.auth.login
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import com.mulkkam.ui.auth.login.model.AuthPlatform
import org.koin.compose.viewmodel.koinViewModel

@Composable
expect fun LoginRoute(
    padding: PaddingValues,
    onNavigateToOnboarding: () -> Unit,
    onNavigateToMain: () -> Unit,
    onLogin: (
        authPlatform: AuthPlatform,
        onSuccess: (token: String) -> Unit,
        onError: (errorMessage: String) -> Unit,
    ) -> Unit,
    appVersion: String,
    snackbarHostState: SnackbarHostState,
    viewModel: LoginViewModel = koinViewModel(),
)
