package com.mulkkam.ui.onboarding.nickname

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
internal fun NicknameScreen(
    padding: PaddingValues,
    onboardingInfo: OnboardingInfo?,
    onNavigateToBack: () -> Boolean,
    onNavigateToBioInfo: (onboardingInfo: OnboardingInfo) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(text = "Onboarding - Nickname", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = {
            val updatedInfo = (onboardingInfo ?: OnboardingInfo()).copy(nickname = "TestUser")
            onNavigateToBioInfo(updatedInfo)
        }) {
            Text("Next → Go to BioInfo")
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(onClick = { onNavigateToBack() }) {
            Text("Back")
        }
    }
}
