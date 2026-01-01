package com.mulkkam.ui.friends.friends.component

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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mulkkam.domain.model.friend.Friend
import com.mulkkam.ui.designsystem.Black
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.White
import com.mulkkam.ui.friends.model.FriendsDisplayMode
import mulkkam.shared.generated.resources.Res
import mulkkam.shared.generated.resources.friends_delete_friend_content_description
import mulkkam.shared.generated.resources.friends_throw_water_balloon_content_description
import mulkkam.shared.generated.resources.ic_friends_delete
import mulkkam.shared.generated.resources.ic_friends_water_balloon
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

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
                        painter = painterResource(Res.drawable.ic_friends_water_balloon),
                        contentDescription = stringResource(Res.string.friends_throw_water_balloon_content_description),
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
                        painter = painterResource(Res.drawable.ic_friends_delete),
                        contentDescription =
                            stringResource(
                                Res.string.friends_delete_friend_content_description,
                                friend.nickname,
                            ),
                        tint = Color.Unspecified,
                    )
                }
            }
        }
    }
}
