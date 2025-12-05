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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mulkkam.R
import com.mulkkam.domain.model.bio.BioWeight.Companion.WEIGHT_DEFAULT
import com.mulkkam.ui.component.MulKkamToastHost
import com.mulkkam.ui.component.MulKkamToastState
import com.mulkkam.ui.component.SaveButton
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
import com.mulkkam.ui.settingbioinfo.dialog.SettingWeightBottomSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingBioInfoScreen(
    navigateToBack: () -> Unit,
    navigateToHealthConnect: () -> Unit,
    viewModel: SettingBioInfoViewModel = hiltViewModel(),
) {
    var isShowBottomSheet by rememberSaveable { mutableStateOf(false) }
    val modalBottomSheetState = rememberModalBottomSheetState()

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
        if (isShowBottomSheet) {
            SettingWeightBottomSheet(
                initialWeight = weight?.value ?: WEIGHT_DEFAULT,
                sheetState = modalBottomSheetState,
                onDismiss = { isShowBottomSheet = false },
                onSave = { weight -> viewModel.updateWeight(weight) },
            )
        }

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
                    onClickSection = { isShowBottomSheet = true },
                )

                HealthConnectSection(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 34.dp, start = 24.dp, end = 24.dp),
                    onClick = { navigateToHealthConnect() },
                )
            }

            SaveButton(
                onClick = { viewModel.saveBioInfo() },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 24.dp)
                        .align(Alignment.BottomCenter),
                enabled = gender != null && weight != null && bioInfoChangeUiState !is MulKkamUiState.Loading,
            )

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
        )
    }
}
