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
) {
    TargetAmountScreen(
        padding = padding,
        onboardingInfo = onboardingInfo,
        onNavigateToBack = onNavigateToBack,
        onNavigateToCups = onNavigateToCups,
    )
}
