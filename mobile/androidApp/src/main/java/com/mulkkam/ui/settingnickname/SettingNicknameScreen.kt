package com.mulkkam.ui.settingnickname

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mulkkam.R
import com.mulkkam.domain.model.members.Nickname
import com.mulkkam.domain.model.result.MulKkamError
import com.mulkkam.ui.component.MulKkamSnackbarHost2
import com.mulkkam.ui.component.MulKkamToastHost2
import com.mulkkam.ui.component.MulKkamToastState2
import com.mulkkam.ui.component.SaveButton2
import com.mulkkam.ui.component.rememberMulKkamToastState2
import com.mulkkam.ui.component.showMulKkamSnackbar2
import com.mulkkam.ui.designsystem.Gray400
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.White
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.model.NicknameValidationUiState
import com.mulkkam.ui.settingnickname.component.NicknameInputSection
import com.mulkkam.ui.settingnickname.component.SettingNicknameTopAppBar
import com.mulkkam.ui.util.extensions.collectWithLifecycle
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingNicknameScreen(
    navigateToBack: () -> Unit,
    viewModel: SettingNicknameViewModel = koinViewModel(),
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    var nickname: String by remember { mutableStateOf("") }
    val nicknameValidationUiState: NicknameValidationUiState by viewModel.nicknameValidationState.collectAsStateWithLifecycle()
    val onNicknameValidationError: MulKkamError? by viewModel.nicknameValidationError.collectAsStateWithLifecycle()

    val toastState: MulKkamToastState2 = rememberMulKkamToastState2()
    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }

    viewModel.originalNicknameUiState.collectWithLifecycle(lifecycleOwner) { state ->
        if (state is MulKkamUiState.Success<Nickname>) {
            nickname = state.data.name
        }
    }

    viewModel.nicknameChangeUiState.collectWithLifecycle(lifecycleOwner) { state ->
        when (state) {
            is MulKkamUiState.Success<Unit> -> {
                toastState.showMulKkamToast2(
                    message = context.getString(R.string.setting_nickname_change_complete),
                    iconResourceId = R.drawable.ic_info_circle,
                )
                navigateToBack()
            }

            is MulKkamUiState.Loading, MulKkamUiState.Idle -> Unit

            is MulKkamUiState.Failure ->
                snackbarHostState.showMulKkamSnackbar2(
                    message = context.getString(R.string.network_check_error),
                    iconResourceId = R.drawable.ic_alert_circle,
                )
        }
    }

    Scaffold(
        topBar = {
            SettingNicknameTopAppBar { navigateToBack() }
        },
        containerColor = White,
        modifier =
            Modifier
                .background(White)
                .systemBarsPadding(),
    ) { innerPadding ->
        Box(
            modifier =
                Modifier
                    .fillMaxSize(),
        ) {
            Column(
                modifier =
                    Modifier
                        .padding(innerPadding)
                        .padding(24.dp),
            ) {
                Text(
                    text = stringResource(R.string.setting_nickname_edit_nickname_label),
                    style = MulKkamTheme.typography.title2,
                    color = Gray400,
                )

                NicknameInputSection(
                    nickname = nickname,
                    nicknameValidationState = nicknameValidationUiState,
                    nicknameError = onNicknameValidationError,
                    onNicknameChange = {
                        nickname = it
                        viewModel.updateNickname(nickname)
                    },
                    onCheckDuplicate = { viewModel.checkNicknameAvailability(nickname) },
                )
            }

            SaveButton2(
                onClick = { viewModel.saveNickname(nickname) },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(24.dp),
                enabled = nicknameValidationUiState == NicknameValidationUiState.VALID,
            )

            MulKkamSnackbarHost2(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter),
            )
            MulKkamToastHost2(
                state = toastState,
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingNicknameScreenPreview() {
    MulKkamTheme {
        SettingNicknameScreen(navigateToBack = {})
    }
}
