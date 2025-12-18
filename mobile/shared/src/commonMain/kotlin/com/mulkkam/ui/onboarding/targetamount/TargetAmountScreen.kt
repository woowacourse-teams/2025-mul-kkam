package com.mulkkam.ui.onboarding.targetamount

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mulkkam.domain.model.OnboardingInfo

@Composable
internal fun TargetAmountScreen(
    padding: PaddingValues,
    onboardingInfo: OnboardingInfo,
    onNavigateBack: () -> Boolean,
    onNavigateToCups: (onboardingInfo: OnboardingInfo) -> Unit,
) {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(padding),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = "Onboarding - TargetAmount Screen")
    }
}
