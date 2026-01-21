package com.mulkkam.ui.onboarding.terms

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.mulkkam.domain.model.OnboardingInfo
import com.mulkkam.ui.util.extensions.openLink

@Composable
actual fun TermsRoute(
    padding: PaddingValues,
    onNavigateToBack: () -> Boolean,
    onNavigateToNickname: (onboardingInfo: OnboardingInfo) -> Unit,
    currentProgress: Int,
) {
    val context = LocalContext.current

    TermsScreen(
        padding = padding,
        navigateToBack = onNavigateToBack,
        loadToPage = { context.openLink(it) },
        navigateToNextStep = onNavigateToNickname,
        currentProgress = currentProgress,
    )
}
