package com.mulkkam.ui.onboarding.terms

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import com.mulkkam.domain.model.OnboardingInfo
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationOpenURLOptionsKey

@Composable
actual fun TermsRoute(
    padding: PaddingValues,
    onNavigateToBack: () -> Boolean,
    onNavigateToNickname: (onboardingInfo: OnboardingInfo?) -> Unit,
) {
    TermsScreen(
        padding = padding,
        navigateToBack = onNavigateToBack,
        loadToPage = { openUrl(it) },
        navigateToNextStep = onNavigateToNickname,
        currentProgress = 1,
    )
}

private fun openUrl(url: String) {
    val nsUrl = NSURL.URLWithString(url) ?: return
    UIApplication.sharedApplication.openURL(
        nsUrl,
        options = emptyMap<Any?, UIApplicationOpenURLOptionsKey>(),
        completionHandler = null,
    )
}
