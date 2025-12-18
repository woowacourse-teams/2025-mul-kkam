package com.mulkkam.ui.onboarding.cups

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
fun CupsScreen(
    padding: PaddingValues,
    onboardingInfo: OnboardingInfo,
    onNavigateToBack: () -> Boolean,
    onNavigateToMain: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(padding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(text = "Onboarding - Cups", fontSize = 24.sp)
        Text(text = "Nickname: ${onboardingInfo.nickname ?: "N/A"}", fontSize = 14.sp)
        Text(text = "Target: ${onboardingInfo.targetAmount ?: "N/A"} ml", fontSize = 14.sp)
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onNavigateToMain) {
            Text("Complete → Go to Home")
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(onClick = { onNavigateToBack() }) {
            Text("Back")
        }
    }
}
