package com.mulkkam.ui.settingbioinfo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mulkkam.R
import com.mulkkam.ui.component.MulKkamToastHost
import com.mulkkam.ui.component.MulKkamToastState
import com.mulkkam.ui.component.rememberMulKkamToastState
import com.mulkkam.ui.designsystem.Gray200
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.MulkkamTheme
import com.mulkkam.ui.designsystem.Primary200
import com.mulkkam.ui.designsystem.White
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.settingbioinfo.component.GenderSection
import com.mulkkam.ui.settingbioinfo.component.HealthConnectSection
import com.mulkkam.ui.settingbioinfo.component.SettingBioInfoTopAppBar
import com.mulkkam.ui.settingbioinfo.component.WeightSection

@Composable
fun SettingBioInfoScreen(
    navigateToBack: () -> Unit,
    navigateToHealthConnect: () -> Unit,
    onClickWeightSection: () -> Unit,
    viewModel: SettingBioInfoViewModel = hiltViewModel(),
) {
    val context = LocalContext.current

    val gender by viewModel.gender.collectAsStateWithLifecycle()
    val weight by viewModel.weight.collectAsStateWithLifecycle()
    val bioInfoChangeUiState by viewModel.bioInfoChangeUiState.collectAsStateWithLifecycle()

    val toastState: MulKkamToastState = rememberMulKkamToastState()

    LaunchedEffect(bioInfoChangeUiState) {
        when (bioInfoChangeUiState) {
            is MulKkamUiState.Success<Unit> -> {
                toastState.showMulKkamToast(
                    message = context.getString(R.string.setting_bio_info_complete_description),
                    iconResourceId = R.drawable.ic_info_circle,
                )
                navigateToBack()
            }

            is MulKkamUiState.Failure -> {
                toastState.showMulKkamToast(
                    message = context.getString(R.string.network_check_error),
                    iconResourceId = R.drawable.ic_alert_circle,
                )
            }

            is MulKkamUiState.Loading, MulKkamUiState.Idle -> Unit
        }
    }

    Scaffold(
        topBar = { SettingBioInfoTopAppBar(navigateToBack) },
        containerColor = White,
        modifier =
            Modifier
                .background(White)
                .systemBarsPadding(),
    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxHeight(),
        ) {
            Column(
                modifier = Modifier.padding(innerPadding),
            ) {
                GenderSection(
                    gender = gender,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 34.dp, start = 24.dp, end = 24.dp),
                    onClickGender = { viewModel.updateGender(it) },
                )

                WeightSection(
                    weight = weight,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp, start = 24.dp, end = 24.dp),
                    onClickSection = { onClickWeightSection() },
                )

                HealthConnectSection(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 34.dp, start = 24.dp, end = 24.dp),
                    onClick = { navigateToHealthConnect() },
                )
            }

            Button(
                onClick = { viewModel.saveBioInfo() },
                enabled = gender != null && weight != null && bioInfoChangeUiState !is MulKkamUiState.Loading,
                colors =
                    ButtonColors(
                        containerColor = Primary200,
                        contentColor = White,
                        disabledContainerColor = Gray200,
                        disabledContentColor = White,
                    ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(0.dp),
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 24.dp)
                        .align(Alignment.BottomCenter),
            ) {
                Text(
                    text = stringResource(R.string.setting_save),
                    style = MulKkamTheme.typography.title2,
                    modifier = Modifier.padding(vertical = 14.dp),
                )
            }

            MulKkamToastHost(
                state = toastState,
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingBioInfoScreenPreview() {
    MulkkamTheme {
        SettingBioInfoScreen(
            navigateToBack = {},
            navigateToHealthConnect = {},
            onClickWeightSection = {},
        )
    }
}
