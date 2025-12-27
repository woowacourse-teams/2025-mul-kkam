package com.mulkkam.ui.onboarding.cups

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import com.mulkkam.domain.model.OnboardingInfo

@Composable
fun CupsRoute(
    padding: PaddingValues,
    onboardingInfo: OnboardingInfo,
    onNavigateToBack: () -> Boolean,
    onNavigateToMain: () -> Unit,
) {
    CupsScreen(
        padding = padding,
        onboardingInfo = onboardingInfo,
        onNavigateToBack = onNavigateToBack,
        onNavigateToMain = onNavigateToMain,
    )
}
