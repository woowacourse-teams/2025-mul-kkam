package com.mulkkam.ui.onboarding

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import com.mulkkam.ui.navigation.MainNavigator
import com.mulkkam.ui.navigation.OnboardingRoute

object OnboardingNavGraph {
    @Composable
    fun entryProvider(
        route: com.mulkkam.ui.navigation.OnboardingRoute,
        padding: PaddingValues,
        navigator: com.mulkkam.ui.navigation.MainNavigator,
    ): com.mulkkam.ui.core.NavEntry<com.mulkkam.ui.navigation.OnboardingRoute> =
        when (route) {
            is com.mulkkam.ui.navigation.OnboardingRoute.Terms -> {
                _root_ide_package_.com.mulkkam.ui.core.entry(route) {
                    _root_ide_package_.com.mulkkam.ui.onboarding.terms.TermsScreen(
                        padding = padding,
                        onNavigateToNickname = com.mulkkam.ui.navigation.MainNavigator::navigateToOnboardingNickname,
                    )
                }
            }

            is com.mulkkam.ui.navigation.OnboardingRoute.Nickname -> {
                _root_ide_package_.com.mulkkam.ui.core.entry(route) {
                    _root_ide_package_.com.mulkkam.ui.onboarding.nickname.NicknameScreen(
                        padding = padding,
                        onNavigateBack = com.mulkkam.ui.navigation.MainNavigator::popBackStack,
                        onNavigateToBioInfo = com.mulkkam.ui.navigation.MainNavigator::navigateToOnboardingBioInfo,
                    )
                }
            }

            is com.mulkkam.ui.navigation.OnboardingRoute.BioInfo -> {
                _root_ide_package_.com.mulkkam.ui.core.entry(route) {
                    _root_ide_package_.com.mulkkam.ui.onboarding.bioinfo.BioInfoScreen(
                        padding = padding,
                        onNavigateBack = com.mulkkam.ui.navigation.MainNavigator::popBackStack,
                        onNavigateToTargetAmount = com.mulkkam.ui.navigation.MainNavigator::navigateToOnboardingTargetAmount,
                    )
                }
            }

            is com.mulkkam.ui.navigation.OnboardingRoute.TargetAmount -> {
                _root_ide_package_.com.mulkkam.ui.core.entry(route) {
                    _root_ide_package_.com.mulkkam.ui.onboarding.targetamount.TargetAmountScreen(
                        padding = padding,
                        onNavigateBack = com.mulkkam.ui.navigation.MainNavigator::popBackStack,
                        onNavigateToCups = com.mulkkam.ui.navigation.MainNavigator::navigateToOnboardingCups,
                    )
                }
            }

            is com.mulkkam.ui.navigation.OnboardingRoute.Cups -> {
                _root_ide_package_.com.mulkkam.ui.core.entry(route) {
                    _root_ide_package_.com.mulkkam.ui.onboarding.cups.CupsScreen(
                        padding = padding,
                        onNavigateBack = com.mulkkam.ui.navigation.MainNavigator::popBackStack,
                        onNavigateToMain = com.mulkkam.ui.navigation.MainNavigator::navigateToHome,
                    )
                }
            }
        }
}
