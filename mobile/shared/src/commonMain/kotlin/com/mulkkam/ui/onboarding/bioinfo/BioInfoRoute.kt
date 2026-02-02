package com.mulkkam.ui.onboarding.bioinfo

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import com.mulkkam.domain.model.OnboardingInfo
import org.koin.core.scope.Scope

@Composable
fun BioInfoRoute(
    padding: PaddingValues,
    onboardingInfo: OnboardingInfo,
    onNavigateToBack: () -> Unit,
    onNavigateToTargetAmount: (onboardingInfo: OnboardingInfo) -> Unit,
    currentProgress: Int,
    onboardingScope: Scope,
) {
    BioInfoScreen(
        padding = padding,
        onboardingInfo = onboardingInfo,
        navigateToBack = onNavigateToBack,
        navigateToNextStep = onNavigateToTargetAmount,
        skipBioInfo = onNavigateToTargetAmount,
        currentProgress = currentProgress,
        onboardingScope = onboardingScope,
    )
}
