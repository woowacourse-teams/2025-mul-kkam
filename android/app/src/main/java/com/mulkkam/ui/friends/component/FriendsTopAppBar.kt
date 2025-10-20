package com.mulkkam.ui.friends.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mulkkam.R
import com.mulkkam.ui.designsystem.Gray400
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.MulkkamTheme
import com.mulkkam.ui.designsystem.Secondary200
import com.mulkkam.ui.designsystem.White

private const val MAX_DISPLAY_FRIEND_REQUEST_COUNT: Int = 99
private const val MIN_DISPLAY_FRIEND_REQUEST_COUNT: Int = 0

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendsTopAppBar(
    onSearchClick: () -> Unit,
    onFriendRequestsClick: () -> Unit,
    friendRequestCount: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .background(White)
                .padding(start = 24.dp, end = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.friends_title),
            style = MulKkamTheme.typography.headline1,
            color = Gray400,
        )
        Spacer(modifier = Modifier.weight(1f))
        IconButton(
            onClick = onSearchClick,
            modifier = Modifier.size(48.dp),
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_friends_search),
                contentDescription = stringResource(R.string.friends_search_button_description),
                modifier = Modifier.padding(12.dp),
            )
        }
        FriendRequestIconButton(
            count = friendRequestCount,
            onClick = onFriendRequestsClick,
        )
    }
}

@Composable
private fun FriendRequestIconButton(
    count: Int,
    onClick: () -> Unit,
) {
    val displayCount: String =
        when {
            count > MAX_DISPLAY_FRIEND_REQUEST_COUNT -> stringResource(R.string.friends_friend_request_count_overflow)
            else -> count.toString()
        }

    Box {
        IconButton(
            onClick = onClick,
            modifier = Modifier.size(48.dp),
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_friends_requests),
                contentDescription =
                    when {
                        count > MIN_DISPLAY_FRIEND_REQUEST_COUNT -> {
                            stringResource(
                                R.string.friends_friend_request_button_description_with_count,
                                displayCount,
                            )
                        }

                        else -> stringResource(R.string.friends_friend_request_button_description)
                    },
                modifier = Modifier.padding(12.dp),
            )
        }
        if (count > MIN_DISPLAY_FRIEND_REQUEST_COUNT) {
            Box(
                modifier =
                    Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 4.dp, end = 4.dp)
                        .size(14.dp)
                        .background(
                            color = Secondary200,
                            shape = CircleShape,
                        ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = displayCount,
                    color = White,
                    style = MulKkamTheme.typography.label2,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                )
            }
        }
    }
}

@Preview(showBackground = false)
@Composable
private fun FriendsTopAppBarPreview() {
    MulkkamTheme {
        FriendsTopAppBar(
            onSearchClick = {},
            onFriendRequestsClick = {},
            friendRequestCount = 5,
        )
    }
}
