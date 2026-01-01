package com.mulkkam.ui.auth.login

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable

@Composable
expect fun LoginScreen(
    padding: PaddingValues,
    onLoginClick: () -> Unit,
    isLoginLoading: Boolean,
    snackbarHostState: SnackbarHostState,
)
