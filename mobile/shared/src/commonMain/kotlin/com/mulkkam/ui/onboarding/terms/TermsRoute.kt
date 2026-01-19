package com.mulkkam.ui.onboarding.terms

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import com.mulkkam.domain.model.OnboardingInfo

@Composable
expect fun TermsRoute(
    padding: PaddingValues,
    onNavigateToBack: () -> Boolean,
    onNavigateToNickname: (onboardingInfo: OnboardingInfo) -> Unit,
    currentProgress: Int,
)
