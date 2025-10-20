package com.mulkkam.ui.friends.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mulkkam.R
import com.mulkkam.domain.model.friend.Friend
import com.mulkkam.ui.designsystem.Black
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.MulkkamTheme
import com.mulkkam.ui.designsystem.White
import com.mulkkam.ui.friends.model.FriendsDisplayMode

@Composable
fun FriendItem(
    friend: Friend,
    displayMode: FriendsDisplayMode,
    onThrowWaterBalloon: () -> Unit,
    onDeleteFriend: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .background(White)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(modifier = Modifier.size(8.dp))
        Text(
            text = friend.nickname,
            style = MulKkamTheme.typography.title1,
            color = Black,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        when (displayMode) {
            FriendsDisplayMode.VIEWING -> {
                IconButton(
                    onClick = onThrowWaterBalloon,
                    modifier = Modifier.size(48.dp),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_friends_water_balloon),
                        contentDescription = stringResource(R.string.friends_throw_water_balloon_content_description),
                        tint = Color.Unspecified,
                    )
                }
            }

            FriendsDisplayMode.EDITING -> {
                IconButton(
                    onClick = onDeleteFriend,
                    modifier = Modifier.size(48.dp),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_friendds_delete),
                        contentDescription =
                            stringResource(
                                R.string.friends_delete_friend_content_description,
                                friend.nickname,
                            ),
                        tint = Color.Unspecified,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FriendItemViewingPreview() {
    MulkkamTheme {
        FriendItem(
            friend = Friend(id = 1L, nickname = "공백"),
            displayMode = FriendsDisplayMode.VIEWING,
            onThrowWaterBalloon = {},
            onDeleteFriend = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FriendItemEditingPreview() {
    MulkkamTheme {
        FriendItem(
            friend = Friend(id = 1L, nickname = "공백"),
            displayMode = FriendsDisplayMode.EDITING,
            onThrowWaterBalloon = {},
            onDeleteFriend = {},
        )
    }
}
