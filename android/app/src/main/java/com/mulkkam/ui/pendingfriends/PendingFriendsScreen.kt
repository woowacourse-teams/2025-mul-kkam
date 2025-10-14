package com.mulkkam.ui.pendingfriends

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mulkkam.ui.designsystem.Gray100
import com.mulkkam.ui.designsystem.MulkkamTheme
import com.mulkkam.ui.designsystem.White
import com.mulkkam.ui.pendingfriends.component.PendingFriendsItem
import com.mulkkam.ui.pendingfriends.component.PendingFriendsTopAppBar

@Composable
fun PendingFriendsScreen(
    navigateToBack: () -> Unit,
    viewModel: PendingFriendsViewModel = viewModel(),
) {
    val pendingFriends by viewModel.pendingFriends.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            PendingFriendsTopAppBar(navigateToBack, pendingFriends.size)
        },
        containerColor = White,
    ) { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding)) {
            items(
                pendingFriends.size,
                key = { pendingFriends[it].name },
            ) { index ->
                PendingFriendsItem(
                    pendingFriend = pendingFriends[index],
                    onAccept = { viewModel.acceptFriend() },
                    onDecline = { viewModel.declineFriend() },
                )

                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    thickness = 1.dp,
                    color = Gray100,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PendingFriendsScreenPreview() {
    MulkkamTheme {
        PendingFriendsScreen(navigateToBack = {})
    }
}
