package com.mulkkam.ui.home.encyclopedia

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationOpenURLOptionsKey

@Composable
actual fun EncyclopediaRoute(
    padding: PaddingValues,
    onNavigateToBack: () -> Boolean,
) {
    EncyclopediaScreen(
        padding =
            PaddingValues(
                start =
                    padding.calculateStartPadding(
                        layoutDirection = LocalLayoutDirection.current,
                    ),
                top = 0.dp,
                end =
                    padding.calculateEndPadding(
                        layoutDirection = LocalLayoutDirection.current,
                    ),
                bottom = padding.calculateBottomPadding(),
            ),
        navigateToBack = onNavigateToBack,
        navigateToInformationSource = { openUrl(it) },
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
