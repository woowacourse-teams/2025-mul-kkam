package com.mulkkam.ui.auth.login

import android.content.Context
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.mulkkam.domain.logger.Logger
import com.mulkkam.domain.model.UserAuthState
import com.mulkkam.domain.model.UserAuthState.ACTIVE_USER
import com.mulkkam.domain.model.UserAuthState.UNONBOARDED
import com.mulkkam.domain.model.logger.LogEvent
import com.mulkkam.ui.auth.login.model.AuthPlatform
import com.mulkkam.ui.component.showMulKkamSnackbar
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
    val context = LocalContext.current
    val logger: Logger = koinInject()

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

    LoginScreen(
        padding = padding,
        onLoginClick = { authPlatform ->
            when (authPlatform) {
                AuthPlatform.KAKAO -> {
                    loginWithKakao(context, logger) { token ->
                        viewModel.loginWithKakao(token)
                    }
                }

                else -> Unit
            }
        },
        snackbarHostState = snackbarHostState,
        isLoginLoading = isLoginLoading,
    )
}

private fun loginWithKakao(
    context: Context,
    logger: Logger,
    handleLogin: (accessToken: String) -> Unit,
) {
    logger.info(LogEvent.USER_AUTH, "Kakao Login Attempted")
    if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
        loginWithKakaoTalk(context, logger, handleLogin)
    } else {
        loginWithKakaoAccount(context, logger, handleLogin)
    }
}

private fun loginWithKakaoTalk(
    context: Context,
    logger: Logger,
    handleLogin: (accessToken: String) -> Unit,
) {
    UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
        when {
            error is ClientError && error.reason == ClientErrorCause.Cancelled -> Unit

            error != null -> {
                logger.error(LogEvent.USER_AUTH, "Kakao Login Failed: ${error.message}")
                loginWithKakaoAccount(context, logger, handleLogin)
            }

            else -> {
                token?.let {
                    handleLogin(it.accessToken)
                }
            }
        }
    }
}

private fun loginWithKakaoAccount(
    context: Context,
    logger: Logger,
    handleLogin: (accessToken: String) -> Unit,
) {
    UserApiClient.instance.loginWithKakaoAccount(context) { token, error ->
        if (error != null) {
            logger.error(LogEvent.USER_AUTH, "Kakao Login Failed: ${error.message}")
        } else {
            token?.let {
                handleLogin(it.accessToken)
            }
        }
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
