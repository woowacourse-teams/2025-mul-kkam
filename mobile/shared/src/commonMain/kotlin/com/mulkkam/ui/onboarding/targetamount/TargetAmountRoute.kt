package com.mulkkam.ui.onboarding.targetamount

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import com.mulkkam.domain.model.OnboardingInfo

@Composable
fun TargetAmountRoute(
    padding: PaddingValues,
    onboardingInfo: OnboardingInfo,
    onNavigateToBack: () -> Boolean,
    onNavigateToCups: (onboardingInfo: OnboardingInfo) -> Unit,
    currentProgress: Int,
) {
    TargetAmountScreen(
        padding = padding,
        onboardingInfo = onboardingInfo,
        navigateToBack = onNavigateToBack,
        navigateToNextStep = onNavigateToCups,
        currentProgress = currentProgress,
    )
}
