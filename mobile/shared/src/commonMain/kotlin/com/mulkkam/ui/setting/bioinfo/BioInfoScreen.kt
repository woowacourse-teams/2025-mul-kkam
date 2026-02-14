package com.mulkkam.ui.setting.bioinfo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mulkkam.domain.model.bio.BioWeight.Companion.WEIGHT_DEFAULT
import com.mulkkam.ui.component.SaveButton
import com.mulkkam.ui.component.showMulKkamSnackbar
import com.mulkkam.ui.designsystem.White
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.setting.bioinfo.component.GenderSection
import com.mulkkam.ui.setting.bioinfo.component.HealthSection
import com.mulkkam.ui.setting.bioinfo.component.SettingBioInfoTopAppBar
import com.mulkkam.ui.setting.bioinfo.component.SettingWeightBottomSheet
import com.mulkkam.ui.setting.bioinfo.component.WeightSection
import com.mulkkam.ui.util.extensions.collectWithLifecycle
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import mulkkam.shared.generated.resources.Res
import mulkkam.shared.generated.resources.ic_alert_circle
import mulkkam.shared.generated.resources.ic_info_circle
import mulkkam.shared.generated.resources.network_check_error
import mulkkam.shared.generated.resources.setting_bio_info_complete_description
import org.jetbrains.compose.resources.getString
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BioInfoScreen(
    padding: PaddingValues,
    navigateToBack: () -> Unit,
    navigateToHealth: suspend () -> Unit,
    snackbarHostState: SnackbarHostState,
    viewModel: SettingBioInfoViewModel = koinViewModel(),
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    var isShowBottomSheet by rememberSaveable { mutableStateOf(false) }
    val modalBottomSheetState = rememberModalBottomSheetState()

    val gender by viewModel.gender.collectAsStateWithLifecycle()
    val weight by viewModel.weight.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.onBioInfoChanged.collectWithLifecycle(lifecycleOwner) { state ->
            handleBioInfoChangedAction(state, navigateToBack, snackbarHostState)
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        topBar = { SettingBioInfoTopAppBar(navigateToBack) },
        containerColor = White,
        modifier = Modifier.fillMaxSize().background(White).padding(padding),
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

                HealthSection(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 34.dp, start = 24.dp, end = 24.dp),
                    onClick = { coroutineScope.launch { navigateToHealth() } },
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
                enabled = gender != null && weight != null,
            )
        }
    }
}

private suspend fun handleBioInfoChangedAction(
    state: MulKkamUiState<Unit>,
    navigateToBack: () -> Unit,
    snackbarHostState: SnackbarHostState,
) {
    when (state) {
        is MulKkamUiState.Success<Unit> -> {
            coroutineScope {
                launch {
                    snackbarHostState.showMulKkamSnackbar(
                        message = getString(resource = Res.string.setting_bio_info_complete_description),
                        iconResource = Res.drawable.ic_info_circle,
                    )
                }
                navigateToBack()
            }
        }

        is MulKkamUiState.Failure -> {
            snackbarHostState.showMulKkamSnackbar(
                message = getString(resource = Res.string.network_check_error),
                iconResource = Res.drawable.ic_alert_circle,
            )
        }

        is MulKkamUiState.Loading, MulKkamUiState.Idle -> Unit
    }
}
