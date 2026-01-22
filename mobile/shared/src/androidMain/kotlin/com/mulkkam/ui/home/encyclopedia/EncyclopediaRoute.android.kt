package com.mulkkam.ui.home.encyclopedia

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.mulkkam.ui.util.extensions.openLink
import mulkkam.shared.generated.resources.Res
import mulkkam.shared.generated.resources.coffee_encyclopedia_source

@Composable
actual fun EncyclopediaRoute(
    padding: PaddingValues,
    onNavigateToBack: () -> Boolean,
) {
    val context = LocalContext.current

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
        navigateToInformationSource = { context.openLink(it) },
    )
}
