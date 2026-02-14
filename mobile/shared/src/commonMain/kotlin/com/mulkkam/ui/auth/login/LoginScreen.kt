package com.mulkkam.ui.auth.login

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import com.mulkkam.ui.auth.login.model.AuthPlatform

@Composable
expect fun LoginScreen(
    padding: PaddingValues,
    onLoginClick: (authPlatform: AuthPlatform) -> Unit,
    isLoginLoading: Boolean,
    snackbarHostState: SnackbarHostState,
    navigateToStoreAndExit: () -> Unit,
    showDialog: Boolean = false,
)
