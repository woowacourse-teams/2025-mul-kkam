package com.mulkkam.ui.onboarding.nickname

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import com.mulkkam.domain.model.OnboardingInfo
import org.koin.core.scope.Scope

@Composable
fun NicknameRoute(
    padding: PaddingValues,
    onboardingInfo: OnboardingInfo,
    onNavigateToBack: () -> Unit,
    onNavigateToBioInfo: (onboardingInfo: OnboardingInfo) -> Unit,
    currentProgress: Int,
    onboardingScope: Scope,
) {
    NicknameScreen(
        padding = padding,
        onboardingInfo = onboardingInfo,
        navigateToBack = onNavigateToBack,
        navigateToNextStep = onNavigateToBioInfo,
        currentProgress = currentProgress,
        onboardingScope = onboardingScope,
    )
}
