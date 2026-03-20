package com.mulkkam.ui.onboarding.cups

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import org.koin.core.scope.Scope

@Composable
fun CupsRoute(
    padding: PaddingValues,
    onNavigateToBack: () -> Unit,
    onNavigateToCoffeeEncyclopedia: () -> Unit,
    onNavigateToMain: () -> Unit,
    currentProgress: Int,
    snackbarHostState: SnackbarHostState,
    onboardingScope: Scope,
) {
    CupsScreen(
        padding = padding,
        navigateToBack = onNavigateToBack,
        navigateToCoffeeEncyclopedia = onNavigateToCoffeeEncyclopedia,
        currentProgress = currentProgress,
        onCompleteOnboarding = onNavigateToMain,
        snackbarHostState = snackbarHostState,
        onboardingScope = onboardingScope,
    )
}
