package com.mulkkam.ui.onboarding.terms

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import com.mulkkam.ui.util.extensions.openLink
import org.koin.core.scope.Scope

@Composable
fun TermsRoute(
    padding: PaddingValues,
    onNavigateToBack: () -> Unit,
    onNavigateToNickname: () -> Unit,
    currentProgress: Int,
    onboardingScope: Scope,
) {
    TermsScreen(
        padding = padding,
        navigateToBack = onNavigateToBack,
        loadToPage = { it.openLink() },
        navigateToNextStep = onNavigateToNickname,
        currentProgress = currentProgress,
        onboardingScope = onboardingScope,
    )
}
