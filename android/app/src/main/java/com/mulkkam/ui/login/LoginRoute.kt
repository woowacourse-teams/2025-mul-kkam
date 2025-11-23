package com.mulkkam.ui.login

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mulkkam.R
import com.mulkkam.ui.component.showMulKkamSnackbar
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.model.UserAuthState
import kotlinx.coroutines.launch

private const val BACK_PRESS_THRESHOLD: Long = 2000L

@Composable
fun LoginRoute(
    viewModel: LoginViewModel,
    onLoginWithKakao: () -> Unit,
    onNavigateToNextScreen: (UserAuthState) -> Unit,
) {
    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val currentOnLoginWithKakao = rememberUpdatedState(newValue = onLoginWithKakao)
    val currentOnNavigateToNextScreen = rememberUpdatedState(newValue = onNavigateToNextScreen)

    val authUiState by viewModel.authUiState.collectAsStateWithLifecycle()
    val isLoginLoading = authUiState is MulKkamUiState.Loading

    val exitMessage = stringResource(R.string.main_main_back_press_exit_message)
    var lastBackPressedTimestamp by remember { mutableLongStateOf(0L) }

    BackHandler {
        val activity = context as? Activity
        val currentTimestamp = System.currentTimeMillis()
        if (currentTimestamp - lastBackPressedTimestamp >= BACK_PRESS_THRESHOLD) {
            lastBackPressedTimestamp = currentTimestamp
            coroutineScope.launch {
                snackbarHostState.showMulKkamSnackbar(
                    message = exitMessage,
                    iconResourceId = R.drawable.ic_info_circle,
                )
            }
        } else {
            activity?.finishAffinity()
        }
    }

    LaunchedEffect(authUiState) {
        when (val state = authUiState) {
            is MulKkamUiState.Success<UserAuthState> ->
                currentOnNavigateToNextScreen.value(state.data)

            is MulKkamUiState.Failure ->
                snackbarHostState.showMulKkamSnackbar(
                    message = context.getString(R.string.network_check_error),
                    iconResourceId = R.drawable.ic_alert_circle,
                )

            is MulKkamUiState.Idle, is MulKkamUiState.Loading -> Unit
        }
    }

    LoginScreen(
        onLoginClick = { currentOnLoginWithKakao.value() },
        snackbarHostState = snackbarHostState,
        isLoginLoading = isLoginLoading,
    )
}
