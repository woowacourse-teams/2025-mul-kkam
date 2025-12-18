package com.mulkkam.ui.onboarding.bioinfo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mulkkam.R
import com.mulkkam.domain.model.bio.BioWeight
import com.mulkkam.domain.model.bio.BioWeight.Companion.WEIGHT_DEFAULT
import com.mulkkam.domain.model.bio.Gender
import com.mulkkam.ui.designsystem.Gray400
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.MulkkamTheme
import com.mulkkam.ui.designsystem.White
import com.mulkkam.ui.onboarding.component.NextButton
import com.mulkkam.ui.onboarding.component.OnboardingTopAppBar
import com.mulkkam.ui.settingbioinfo.component.GenderSection
import com.mulkkam.ui.settingbioinfo.component.SettingWeightBottomSheet
import com.mulkkam.ui.settingbioinfo.component.WeightSection
import com.mulkkam.ui.util.extensions.getStyledText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BioInfoScreen(
    navigateToBack: () -> Unit,
    navigateToNextStep: (gender: Gender?, weight: BioWeight?) -> Unit,
    skipBioInfo: () -> Unit,
    currentProgress: Int,
    viewModel: BioInfoViewModel = viewModel(),
) {
    var isShowBottomSheet by rememberSaveable { mutableStateOf(false) }
    val modalBottomSheetState = rememberModalBottomSheetState()

    val gender by viewModel.gender.collectAsStateWithLifecycle()
    val weight by viewModel.weight.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            OnboardingTopAppBar(
                onBackClick = navigateToBack,
                onSkip = { skipBioInfo() },
                currentProgress = currentProgress,
                canSkip = true,
            )
        },
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

        Column(
            modifier =
                Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(24.dp),
        ) {
            Text(
                text =
                    stringResource(R.string.bio_info_input_hint).getStyledText(
                        style = MulKkamTheme.typography.title1,
                        highlightedText = arrayOf(stringResource(R.string.bio_info_input_hint_highlight)),
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
                onClick = { navigateToNextStep(gender, weight) },
                modifier = Modifier.fillMaxWidth(),
                enabled = gender != null && weight != null,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BioInfoScreenPreview() {
    MulkkamTheme {
        BioInfoScreen(
            navigateToBack = {},
            navigateToNextStep = { _, _ -> },
            skipBioInfo = {},
            currentProgress = 3,
        )
    }
}
