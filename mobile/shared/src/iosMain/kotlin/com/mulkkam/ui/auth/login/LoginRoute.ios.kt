package com.mulkkam.ui.auth.login

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mulkkam.domain.logger.Logger
import com.mulkkam.domain.login.LoginPlatform
import com.mulkkam.domain.model.UserAuthState
import com.mulkkam.domain.model.UserAuthState.ACTIVE_USER
import com.mulkkam.domain.model.UserAuthState.UNONBOARDED
import com.mulkkam.domain.model.logger.LogEvent
import com.mulkkam.ui.auth.login.model.LoginType
import com.mulkkam.ui.component.showMulKkamSnackbar
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.login.LoginViewModel
import com.mulkkam.ui.model.MulKkamUiState
import mulkkam.shared.generated.resources.Res
import mulkkam.shared.generated.resources.ic_alert_circle
import mulkkam.shared.generated.resources.network_check_error
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
actual fun LoginRoute(
    padding: PaddingValues,
    onNavigateToOnboarding: () -> Unit,
    onNavigateToMain: () -> Unit,
    viewModel: LoginViewModel,
) {
    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
    val logger = koinInject<Logger>()

    val authUiState by viewModel.authUiState.collectAsStateWithLifecycle()
    val isLoginLoading = authUiState is MulKkamUiState.Loading
    val networkCheckMessage = stringResource(resource = Res.string.network_check_error)

    LaunchedEffect(authUiState) {
        when (val state = authUiState) {
            is MulKkamUiState.Success<UserAuthState> -> {
                navigateToNextScreen(
                    userAuthState = state.data,
                    onNavigateToOnboarding = onNavigateToOnboarding,
                    onNavigateToMain = onNavigateToMain,
                )
            }

            is MulKkamUiState.Failure -> {
                snackbarHostState.showMulKkamSnackbar(
                    message = networkCheckMessage,
                    iconResource = Res.drawable.ic_alert_circle,
                )
            }

            is MulKkamUiState.Idle, is MulKkamUiState.Loading -> Unit
        }
    }

    MulKkamTheme {
        LoginScreen(
            padding = padding,
            onLoginClick = { loginType ->
                when (loginType) {
                    LoginType.KAKAO -> {
                        LoginPlatform.provider?.loginWithKakao(
                            onSuccess = { token -> viewModel.loginWithKakao(token = token) },
                            onFailure = { error ->
                                logger.error(
                                    LogEvent.USER_AUTH,
                                    "Kakao Login Failed: $error",
                                )
                            },
                        )
                    }

                    LoginType.APPLE -> Unit
                }
            },
            snackbarHostState = snackbarHostState,
            isLoginLoading = isLoginLoading,
        )
    }
}

private fun navigateToNextScreen(
    userAuthState: UserAuthState,
    onNavigateToOnboarding: () -> Unit,
    onNavigateToMain: () -> Unit,
) {
    when (userAuthState) {
        UNONBOARDED -> onNavigateToOnboarding()
        ACTIVE_USER -> onNavigateToMain()
    }
}
