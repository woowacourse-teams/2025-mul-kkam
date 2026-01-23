package com.mulkkam.ui.onboarding.bioinfo

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import com.mulkkam.domain.model.OnboardingInfo

@Composable
fun BioInfoRoute(
    padding: PaddingValues,
    onboardingInfo: OnboardingInfo,
    onNavigateToBack: () -> Unit,
    onNavigateToTargetAmount: (onboardingInfo: OnboardingInfo) -> Unit,
    currentProgress: Int,
) {
    BioInfoScreen(
        padding = padding,
        onboardingInfo = onboardingInfo,
        navigateToBack = onNavigateToBack,
        navigateToNextStep = onNavigateToTargetAmount,
        skipBioInfo = onNavigateToTargetAmount,
        currentProgress = currentProgress,
    )
}
