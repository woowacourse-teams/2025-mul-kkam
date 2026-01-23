package com.mulkkam.ui.setting.bioinfo

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import com.mulkkam.domain.model.bio.HealthPlatform
import org.koin.compose.koinInject

@Composable
fun BioInfoRoute(
    padding: PaddingValues,
    onNavigateToBack: () -> Unit,
    snackbarHostState: SnackbarHostState,
) {
    val healthManager: HealthPlatform = koinInject()
    BioInfoScreen(
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
        navigateToHealth = healthManager::navigateToHealthConnect,
        snackbarHostState = snackbarHostState,
    )
}
