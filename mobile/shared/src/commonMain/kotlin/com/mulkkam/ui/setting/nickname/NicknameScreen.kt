package com.mulkkam.ui.setting.nickname

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.input.key.Key.Companion.R
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mulkkam.domain.model.members.Nickname
import com.mulkkam.domain.model.result.MulKkamError
import com.mulkkam.ui.component.MulKkamSnackbarHost
import com.mulkkam.ui.component.SaveButton
import com.mulkkam.ui.component.showMulKkamSnackbar
import com.mulkkam.ui.designsystem.Gray400
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.White
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.model.NicknameValidationUiState
import com.mulkkam.ui.setting.nickname.component.NicknameInputSection
import com.mulkkam.ui.setting.nickname.component.SettingNicknameTopAppBar
import com.mulkkam.ui.util.extensions.collectWithLifecycle
import mulkkam.shared.generated.resources.Res
import mulkkam.shared.generated.resources.ic_alert_circle
import mulkkam.shared.generated.resources.ic_info_circle
import mulkkam.shared.generated.resources.load_info_error
import mulkkam.shared.generated.resources.network_check_error
import mulkkam.shared.generated.resources.setting_nickname_change_complete
import mulkkam.shared.generated.resources.setting_nickname_edit_nickname_label
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NicknameScreen(
    padding: PaddingValues,
    navigateToBack: () -> Boolean,
    viewModel: SettingNicknameViewModel = koinViewModel(),
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    var nickname: String by remember { mutableStateOf("") }
    val nicknameValidationUiState: NicknameValidationUiState by viewModel.nicknameValidationState.collectAsStateWithLifecycle()
    val onNicknameValidationError: MulKkamError? by viewModel.nicknameValidationError.collectAsStateWithLifecycle()

    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }

    viewModel.originalNicknameUiState.collectWithLifecycle(lifecycleOwner) { state ->
        if (state is MulKkamUiState.Success<Nickname>) {
            nickname = state.data.name
        }
    }

    viewModel.nicknameChangeUiState.collectWithLifecycle(lifecycleOwner) { state ->
        when (state) {
            is MulKkamUiState.Success<Unit> -> {
                snackbarHostState.showMulKkamSnackbar(
                    message = getString(Res.string.setting_nickname_change_complete),
                    iconResource = Res.drawable.ic_info_circle,
                )
                navigateToBack()
            }

            is MulKkamUiState.Loading, MulKkamUiState.Idle -> Unit

            is MulKkamUiState.Failure -> {
                snackbarHostState.showMulKkamSnackbar(
                    message = getString(Res.string.network_check_error),
                    iconResource = Res.drawable.ic_alert_circle,
                )
            }
        }
    }

    Scaffold(
        topBar = {
            SettingNicknameTopAppBar { navigateToBack() }
        },
        containerColor = White,
        modifier = Modifier.background(White),
        snackbarHost = { MulKkamSnackbarHost(hostState = snackbarHostState) },
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
                    text = stringResource(Res.string.setting_nickname_edit_nickname_label),
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

            SaveButton(
                onClick = { viewModel.saveNickname(nickname) },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(24.dp),
                enabled = nicknameValidationUiState == NicknameValidationUiState.VALID,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingNicknameScreenPreview() {
    MulKkamTheme {
        NicknameScreen(
            padding = PaddingValues(),
            navigateToBack = { true },
        )
    }
}
