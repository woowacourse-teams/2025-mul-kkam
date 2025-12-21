package com.mulkkam.ui.onboarding.terms

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import com.mulkkam.domain.model.OnboardingInfo

@Composable
fun TermsRoute(
    padding: PaddingValues,
    onNavigateToNickname: (onboardingInfo: OnboardingInfo?) -> Unit,
) {
    TermsScreen(
        padding = padding,
        onNavigateToNickname = onNavigateToNickname,
    )
}
