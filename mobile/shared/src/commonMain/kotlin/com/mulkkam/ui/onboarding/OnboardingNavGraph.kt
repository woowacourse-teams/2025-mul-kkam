package com.mulkkam.ui.onboarding

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import com.mulkkam.ui.navigation.MainNavigator
import com.mulkkam.ui.navigation.NavEntry
import com.mulkkam.ui.navigation.OnboardingRoute
import com.mulkkam.ui.navigation.entry
import com.mulkkam.ui.onboarding.bioinfo.BioInfoScreen
import com.mulkkam.ui.onboarding.cups.CupsScreen
import com.mulkkam.ui.onboarding.nickname.NicknameScreen
import com.mulkkam.ui.onboarding.targetamount.TargetAmountScreen
import com.mulkkam.ui.onboarding.terms.TermsScreen

object OnboardingNavGraph {
    @Composable
    fun entryProvider(
        route: OnboardingRoute,
        padding: PaddingValues,
        navigator: MainNavigator,
    ): NavEntry<OnboardingRoute> =
        when (route) {
            is OnboardingRoute.Terms -> {
                entry(route) {
                    TermsScreen(
                        padding = padding,
                        onNavigateToNickname = navigator::navigateToOnboardingNickname,
                    )
                }
            }

            is OnboardingRoute.Nickname -> {
                entry(route) {
                    NicknameScreen(
                        padding = padding,
                        onNavigateBack = navigator::popBackStack,
                        onNavigateToBioInfo = navigator::navigateToOnboardingBioInfo,
                    )
                }
            }

            is OnboardingRoute.BioInfo -> {
                entry(route) {
                    BioInfoScreen(
                        padding = padding,
                        onNavigateBack = navigator::popBackStack,
                        onNavigateToTargetAmount = navigator::navigateToOnboardingTargetAmount,
                    )
                }
            }

            is OnboardingRoute.TargetAmount -> {
                entry(route) {
                    TargetAmountScreen(
                        padding = padding,
                        onNavigateBack = navigator::popBackStack,
                        onNavigateToCups = navigator::navigateToOnboardingCups,
                    )
                }
            }

            is OnboardingRoute.Cups -> {
                entry(route) {
                    CupsScreen(
                        padding = padding,
                        onNavigateBack = navigator::popBackStack,
                        onNavigateToMain = navigator::navigateToHome,
                    )
                }
            }
        }
}
