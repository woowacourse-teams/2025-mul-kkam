package com.mulkkam.ui.onboarding.terms

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import com.mulkkam.domain.model.OnboardingInfo
import com.mulkkam.ui.util.extensions.openLink

@Composable
fun TermsRoute(
    padding: PaddingValues,
    onNavigateToBack: () -> Unit,
    onNavigateToNickname: (onboardingInfo: OnboardingInfo) -> Unit,
    currentProgress: Int,
) {
    TermsScreen(
        padding = padding,
        navigateToBack = onNavigateToBack,
        loadToPage = { it.openLink() },
        navigateToNextStep = onNavigateToNickname,
        currentProgress = currentProgress,
    )
}
