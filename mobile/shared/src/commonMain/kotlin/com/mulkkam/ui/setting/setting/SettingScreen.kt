package com.mulkkam.ui.setting.setting

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun SettingScreen(
    padding: PaddingValues,
    onNavigateToAccountInfo: () -> Unit,
    onNavigateToBioInfo: () -> Unit,
    onNavigateToCups: () -> Unit,
    onNavigateToFeedback: () -> Unit,
    onNavigateToNickname: () -> Unit,
    onNavigateToNotification: () -> Unit,
    onNavigateToReminder: () -> Unit,
    onNavigateToTargetAmount: () -> Unit,
    onNavigateToTerms: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        Text(text = "Setting", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onNavigateToAccountInfo) {
            Text("Account Info")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onNavigateToNickname) {
            Text("Nickname")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onNavigateToBioInfo) {
            Text("Bio Info")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onNavigateToTargetAmount) {
            Text("Target Amount")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onNavigateToCups) {
            Text("Cups")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onNavigateToNotification) {
            Text("Notification")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onNavigateToReminder) {
            Text("Reminder")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onNavigateToFeedback) {
            Text("Feedback")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onNavigateToTerms) {
            Text("Terms")
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}
