package com.mulkkam.ui.onboarding

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import com.mulkkam.ui.navigation.MainNavigator
import com.mulkkam.ui.navigation.NavEntry
import com.mulkkam.ui.navigation.OnboardingRoute
import com.mulkkam.ui.navigation.entry
import com.mulkkam.ui.onboarding.bioinfo.BioInfoRoute
import com.mulkkam.ui.onboarding.cups.CupsRoute
import com.mulkkam.ui.onboarding.nickname.NicknameRoute
import com.mulkkam.ui.onboarding.targetamount.TargetAmountRoute
import com.mulkkam.ui.onboarding.terms.TermsRoute
import org.koin.core.scope.Scope

object OnboardingNavGraph {
    @Composable
    fun entryProvider(
        route: OnboardingRoute,
        padding: PaddingValues,
        navigator: MainNavigator,
        snackbarHostState: SnackbarHostState,
        onboardingScope: Scope,
    ): NavEntry<OnboardingRoute> =
        when (route) {
            is OnboardingRoute.Terms -> {
                entry(route) {
                    TermsRoute(
                        padding = padding,
                        onNavigateToNickname = navigator::navigateToOnboardingNickname,
                        onNavigateToBack = navigator::popBackStack,
                        currentProgress = route.currentProgress,
                        onboardingScope = onboardingScope,
                    )
                }
            }

            is OnboardingRoute.Nickname -> {
                entry(route) {
                    NicknameRoute(
                        padding = padding,
                        onNavigateToBack = navigator::popBackStack,
                        onNavigateToBioInfo = navigator::navigateToOnboardingBioInfo,
                        currentProgress = route.currentProgress,
                        onboardingScope = onboardingScope,
                    )
                }
            }

            is OnboardingRoute.BioInfo -> {
                entry(route) {
                    BioInfoRoute(
                        padding = padding,
                        onNavigateToBack = navigator::popBackStack,
                        onNavigateToTargetAmount = navigator::navigateToOnboardingTargetAmount,
                        currentProgress = route.currentProgress,
                        onboardingScope = onboardingScope,
                    )
                }
            }

            is OnboardingRoute.TargetAmount -> {
                entry(route) {
                    TargetAmountRoute(
                        padding = padding,
                        onNavigateToBack = navigator::popBackStack,
                        onNavigateToCups = navigator::navigateToOnboardingCups,
                        currentProgress = route.currentProgress,
                        onboardingScope = onboardingScope,
                    )
                }
            }

            is OnboardingRoute.Cups -> {
                entry(route) {
                    CupsRoute(
                        padding = padding,
                        onNavigateToBack = navigator::popBackStack,
                        onNavigateToCoffeeEncyclopedia = navigator::navigateToEncyclopedia,
                        onNavigateToMain = navigator::navigateToHome,
                        currentProgress = route.currentProgress,
                        snackbarHostState = snackbarHostState,
                        onboardingScope = onboardingScope,
                    )
                }
            }
        }
}
