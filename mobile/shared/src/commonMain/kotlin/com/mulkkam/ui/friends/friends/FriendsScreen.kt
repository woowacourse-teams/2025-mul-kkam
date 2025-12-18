package com.mulkkam.ui.friends.friends

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun FriendsScreen(
    padding: PaddingValues,
    onNavigateToPendingFriends: () -> Unit,
    onNavigateToSearchMembers: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(padding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(text = "Friends", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onNavigateToPendingFriends) {
            Text("Go to Pending Friends")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onNavigateToSearchMembers) {
            Text("Go to Search Members")
        }
    }
}
