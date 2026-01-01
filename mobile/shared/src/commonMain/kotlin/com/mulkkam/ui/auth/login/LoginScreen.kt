package com.mulkkam.ui.auth.login

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import com.mulkkam.ui.auth.login.model.LoginType

@Composable
expect fun LoginScreen(
    padding: PaddingValues,
    onLoginClick: (loginType: LoginType) -> Unit,
    isLoginLoading: Boolean,
    snackbarHostState: SnackbarHostState,
)
