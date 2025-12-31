package com.mulkkam.ui.auth.splash

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.mulkkam.domain.model.UserAuthState
import com.mulkkam.domain.model.UserAuthState.ACTIVE_USER
import com.mulkkam.domain.model.UserAuthState.UNONBOARDED
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.util.extensions.collectWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SplashRoute(
    padding: PaddingValues,
    onNavigateToLogin: () -> Unit,
    onNavigateToMain: () -> Unit,
    onNavigateToOnboarding: () -> Unit,
    viewModel: SplashViewModel = koinViewModel(),
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val isSplashFinished = rememberSaveable { mutableStateOf(false) }
    val hasNavigated = rememberSaveable { mutableStateOf(false) }

    viewModel.authUiState.collectWithLifecycle(lifecycleOwner) { authUiState ->
        if (hasNavigated.value) return@collectWithLifecycle
        navigateToNextScreen(
            isSplashFinished = isSplashFinished.value,
            authUiState = authUiState,
            onNavigateToLogin = {
                hasNavigated.value = true
                onNavigateToLogin()
            },
            onNavigateToMain = {
                hasNavigated.value = true
                onNavigateToMain()
            },
            onNavigateToOnboarding = {
                hasNavigated.value = true
                onNavigateToOnboarding()
            },
        )
    }

    SplashScreen(padding = padding) {
        isSplashFinished.value = true
        navigateToNextScreen(
            isSplashFinished = isSplashFinished.value,
            authUiState = viewModel.authUiState.value,
            onNavigateToLogin = onNavigateToLogin,
            onNavigateToMain = onNavigateToMain,
            onNavigateToOnboarding = onNavigateToOnboarding,
        )
    }
}

private fun navigateToNextScreen(
    isSplashFinished: Boolean,
    authUiState: MulKkamUiState<UserAuthState>,
    onNavigateToLogin: () -> Unit,
    onNavigateToMain: () -> Unit,
    onNavigateToOnboarding: () -> Unit,
) {
    if (isSplashFinished.not()) return

    when (authUiState) {
        is MulKkamUiState.Success<UserAuthState> -> {
            when (authUiState.data) {
                UNONBOARDED -> onNavigateToOnboarding()
                ACTIVE_USER -> onNavigateToMain()
            }
        }

        is MulKkamUiState.Loading -> return
        is MulKkamUiState.Idle -> return
        is MulKkamUiState.Failure -> onNavigateToLogin()
    }
}
