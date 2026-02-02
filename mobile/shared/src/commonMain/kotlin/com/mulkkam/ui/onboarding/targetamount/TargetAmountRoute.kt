package com.mulkkam.ui.onboarding.targetamount

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import com.mulkkam.domain.model.OnboardingInfo
import org.koin.core.scope.Scope

@Composable
fun TargetAmountRoute(
    padding: PaddingValues,
    onNavigateToBack: () -> Unit,
    onNavigateToCups: () -> Unit,
    currentProgress: Int,
    onboardingScope: Scope,
) {
    TargetAmountScreen(
        padding = padding,
        navigateToBack = onNavigateToBack,
        navigateToNextStep = onNavigateToCups,
        currentProgress = currentProgress,
        onboardingScope = onboardingScope,
    )
}
