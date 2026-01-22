package com.mulkkam.ui.onboarding.cups

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import com.mulkkam.domain.model.OnboardingInfo

@Composable
fun CupsRoute(
    padding: PaddingValues,
    onboardingInfo: OnboardingInfo,
    onNavigateToBack: () -> Unit,
    onNavigateToCoffeeEncyclopedia: () -> Unit,
    onNavigateToMain: () -> Unit,
    currentProgress: Int,
    snackbarHostState: SnackbarHostState,
) {
    CupsScreen(
        padding = padding,
        onboardingInfo = onboardingInfo,
        navigateToBack = onNavigateToBack,
        navigateToCoffeeEncyclopedia = onNavigateToCoffeeEncyclopedia,
        currentProgress = currentProgress,
        onCompleteOnboarding = onNavigateToMain,
        snackbarHostState = snackbarHostState,
    )
}
