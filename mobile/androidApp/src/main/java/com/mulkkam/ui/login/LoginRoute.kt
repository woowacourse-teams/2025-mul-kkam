package com.mulkkam.ui.login

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mulkkam.R
import com.mulkkam.domain.model.UserAuthState
import com.mulkkam.ui.component.showMulKkamSnackbar
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.util.extensions.collectWithLifecycle
import kotlinx.coroutines.launch

private const val BACK_PRESS_THRESHOLD: Long = 2000L

@Composable
fun LoginRoute(
    viewModel: LoginViewModel,
    onLoginWithKakao: () -> Unit,
    onNavigateToNextScreen: (UserAuthState) -> Unit,
    onNavigateToPlayStoreAndExit: () -> Unit,
) {
    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val currentOnLoginWithKakao = rememberUpdatedState(newValue = onLoginWithKakao)
    val currentOnNavigateToNextScreen = rememberUpdatedState(newValue = onNavigateToNextScreen)

    val authUiState by viewModel.authUiState.collectAsStateWithLifecycle()
    val isLoginLoading = authUiState is MulKkamUiState.Loading

    var lastBackPressedTimestamp by remember { mutableLongStateOf(0L) }

    var showDialog by remember { mutableStateOf(false) }

    BackHandler {
        val activity = context as? Activity
        val currentTimestamp = System.currentTimeMillis()
        if (currentTimestamp - lastBackPressedTimestamp >= BACK_PRESS_THRESHOLD) {
            lastBackPressedTimestamp = currentTimestamp
            coroutineScope.launch {
                snackbarHostState.showMulKkamSnackbar(
                    message = context.getString(R.string.main_main_back_press_exit_message),
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

    viewModel.isAppOutdated.collectWithLifecycle(lifecycleOwner) { isAppOutdated ->
        if (isAppOutdated) showDialog = true
    }

    LoginScreen(
        onLoginClick = { currentOnLoginWithKakao.value() },
        snackbarHostState = snackbarHostState,
        isLoginLoading = isLoginLoading,
        showDialog = showDialog,
        navigateToPlayStoreAndExit = onNavigateToPlayStoreAndExit,
    )
}
