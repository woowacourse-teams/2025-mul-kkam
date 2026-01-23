package com.mulkkam.ui.pendingfriends.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mulkkam.ui.designsystem.Gray400
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.White
import mulkkam.shared.generated.resources.Res
import mulkkam.shared.generated.resources.ic_common_prev
import mulkkam.shared.generated.resources.pending_friends_top_app_bar_title
import mulkkam.shared.generated.resources.top_app_bar_navigation_icon_description
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PendingFriendsTopAppBar(onBackClick: () -> Unit) {
    Column {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = stringResource(Res.string.pending_friends_top_app_bar_title),
                    style = MulKkamTheme.typography.title2,
                )
            },
            navigationIcon = {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.size(48.dp),
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_common_prev),
                        contentDescription = stringResource(Res.string.top_app_bar_navigation_icon_description),
                    )
                }
            },
            colors =
                TopAppBarDefaults.topAppBarColors(
                    containerColor = White,
                    titleContentColor = Gray400,
                    navigationIconContentColor = Gray400,
                ),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PendingFriendsTopAppBarPreview() {
    MulKkamTheme {
        PendingFriendsTopAppBar(onBackClick = {})
    }
}
