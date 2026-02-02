package com.mulkkam.ui.onboarding.nickname

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import com.mulkkam.domain.model.OnboardingInfo
import org.koin.core.scope.Scope

@Composable
fun NicknameRoute(
    padding: PaddingValues,
    onNavigateToBack: () -> Unit,
    onNavigateToBioInfo: () -> Unit,
    currentProgress: Int,
    onboardingScope: Scope,
) {
    NicknameScreen(
        padding = padding,
        navigateToBack = onNavigateToBack,
        navigateToNextStep = onNavigateToBioInfo,
        currentProgress = currentProgress,
        onboardingScope = onboardingScope,
    )
}
