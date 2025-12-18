package com.mulkkam.ui.onboarding.targetamount

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mulkkam.domain.model.OnboardingInfo

@Composable
internal fun TargetAmountScreen(
    padding: PaddingValues,
    onboardingInfo: OnboardingInfo,
    onNavigateToBack: () -> Boolean,
    onNavigateToCups: (onboardingInfo: OnboardingInfo) -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(padding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(text = "Onboarding - TargetAmount", fontSize = 24.sp)
        Text(text = "Nickname: ${onboardingInfo.nickname ?: "N/A"}", fontSize = 14.sp)
        Text(text = "Weight: ${onboardingInfo.weight ?: "N/A"} kg", fontSize = 14.sp)
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = {
            val updatedInfo = onboardingInfo.copy(targetAmount = 2000)
            onNavigateToCups(updatedInfo)
        }) {
            Text("Next → Go to Cups")
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(onClick = { onNavigateToBack() }) {
            Text("Back")
        }
    }
}
