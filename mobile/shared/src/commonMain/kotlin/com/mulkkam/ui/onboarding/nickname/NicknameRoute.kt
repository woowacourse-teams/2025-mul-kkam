package com.mulkkam.ui.onboarding.nickname

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import com.mulkkam.domain.model.OnboardingInfo

@Composable
fun NicknameRoute(
    padding: PaddingValues,
    onboardingInfo: OnboardingInfo,
    onNavigateToBack: () -> Boolean,
    onNavigateToBioInfo: (onboardingInfo: OnboardingInfo) -> Unit,
    currentProgress: Int,
) {
    NicknameScreen(
        padding = padding,
        onboardingInfo = onboardingInfo,
        navigateToBack = onNavigateToBack,
        navigateToNextStep = onNavigateToBioInfo,
        currentProgress = currentProgress,
    )
}
