package com.mulkkam.ui.onboarding.bioinfo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mulkkam.domain.model.OnboardingInfo
import com.mulkkam.domain.model.bio.BioWeight.Companion.WEIGHT_DEFAULT
import com.mulkkam.ui.designsystem.Gray400
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.White
import com.mulkkam.ui.onboarding.component.NextButton
import com.mulkkam.ui.onboarding.component.OnboardingTopAppBar
import com.mulkkam.ui.setting.bioinfo.component.GenderSection
import com.mulkkam.ui.setting.bioinfo.component.SettingWeightBottomSheet
import com.mulkkam.ui.setting.bioinfo.component.WeightSection
import com.mulkkam.ui.util.extensions.getStyledText
import mulkkam.shared.generated.resources.Res
import mulkkam.shared.generated.resources.bio_info_input_hint
import mulkkam.shared.generated.resources.bio_info_input_hint_highlight
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.scope.Scope

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BioInfoScreen(
    padding: PaddingValues,
    onboardingInfo: OnboardingInfo,
    navigateToBack: () -> Unit,
    navigateToNextStep: (onboardingInfo: OnboardingInfo) -> Unit,
    skipBioInfo: (onboardingInfo: OnboardingInfo) -> Unit,
    currentProgress: Int,
    onboardingScope: Scope,
    viewModel: BioInfoViewModel = koinViewModel(scope = onboardingScope),
) {
    var isShowBottomSheet by rememberSaveable { mutableStateOf(false) }
    val modalBottomSheetState = rememberModalBottomSheetState()

    val gender by viewModel.gender.collectAsStateWithLifecycle()
    val weight by viewModel.weight.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            OnboardingTopAppBar(
                onBackClick = navigateToBack,
                onSkip = {
                    viewModel.clearBioInfo()
                    skipBioInfo(onboardingInfo)
                },
                currentProgress = currentProgress,
                canSkip = true,
            )
        },
        containerColor = White,
        modifier =
            Modifier
                .background(White)
                .padding(padding),
    ) { innerPadding ->
        if (isShowBottomSheet) {
            SettingWeightBottomSheet(
                initialWeight = weight?.value ?: WEIGHT_DEFAULT,
                sheetState = modalBottomSheetState,
                onDismiss = { isShowBottomSheet = false },
                onSave = { weight -> viewModel.updateWeight(weight) },
            )
        }

        Column(
            modifier =
                Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(24.dp),
        ) {
            Text(
                text =
                    stringResource(resource = Res.string.bio_info_input_hint).getStyledText(
                        style = MulKkamTheme.typography.title1,
                        stringResource(resource = Res.string.bio_info_input_hint_highlight),
                    ),
                style = MulKkamTheme.typography.body2,
                color = Gray400,
            )

            GenderSection(
                gender = gender,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 28.dp),
                onClickGender = { viewModel.updateGender(it) },
            )

            WeightSection(
                weight = weight,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp),
                onClickSection = { isShowBottomSheet = true },
            )

            Spacer(modifier = Modifier.weight(1f))

            NextButton(
                onClick = {
                    navigateToNextStep(
                        onboardingInfo.copy(
                            gender = gender,
                            weight = weight,
                        ),
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = gender != null && weight != null,
            )
        }
    }
}
