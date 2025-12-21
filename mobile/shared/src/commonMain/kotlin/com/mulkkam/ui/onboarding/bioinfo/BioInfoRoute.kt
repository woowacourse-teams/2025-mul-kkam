package com.mulkkam.ui.onboarding.bioinfo

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import com.mulkkam.domain.model.OnboardingInfo

@Composable
fun BioInfoRoute(
    padding: PaddingValues,
    onboardingInfo: OnboardingInfo,
    onNavigateToBack: () -> Boolean,
    onNavigateToTargetAmount: (onboardingInfo: OnboardingInfo) -> Unit,
) {
    BioInfoScreen(
        padding = padding,
        onboardingInfo = onboardingInfo,
        onNavigateToBack = onNavigateToBack,
        onNavigateToTargetAmount = onNavigateToTargetAmount,
    )
}
