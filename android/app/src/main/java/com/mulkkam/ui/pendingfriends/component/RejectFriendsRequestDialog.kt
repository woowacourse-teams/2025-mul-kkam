package com.mulkkam.ui.pendingfriends.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.mulkkam.R
import com.mulkkam.domain.model.friends.FriendsRequestInfo
import com.mulkkam.domain.model.members.Nickname
import com.mulkkam.ui.designsystem.Gray400
import com.mulkkam.ui.designsystem.Gray50
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.MulkkamTheme
import com.mulkkam.ui.designsystem.Primary100
import com.mulkkam.ui.designsystem.Secondary200
import com.mulkkam.ui.designsystem.White
import com.mulkkam.ui.util.extensions.noRippleClickable

@Composable
fun RejectFriendsRequestDialog(
    friendsRequest: FriendsRequestInfo,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties =
            DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
            ),
    ) {
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = White,
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_alert_circle),
                    contentDescription = null,
                    tint = Secondary200,
                    modifier = Modifier.size(48.dp),
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text =
                        stringResource(
                            R.string.pending_friends_reject_request_confirmed,
                            friendsRequest.nickname.name,
                        ),
                    color = Gray400,
                    style = MulKkamTheme.typography.title1,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.pending_friends_reject_request_warning),
                    color = Secondary200,
                    style = MulKkamTheme.typography.body2,
                )
                Spacer(modifier = Modifier.height(18.dp))
                Row {
                    Box(
                        modifier =
                            Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Primary100)
                                .noRippleClickable(onClick = onConfirm),
                    ) {
                        Text(
                            modifier =
                                Modifier
                                    .padding(vertical = 10.dp, horizontal = 52.dp),
                            text = stringResource(R.string.pending_friends_cancel_request_confirm),
                            color = White,
                            style = MulKkamTheme.typography.body4,
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Box(
                        modifier =
                            Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Gray50)
                                .noRippleClickable(onClick = onDismiss),
                    ) {
                        Text(
                            modifier =
                                Modifier
                                    .padding(vertical = 10.dp, horizontal = 52.dp),
                            text = stringResource(R.string.pending_friends_cancel_request_cancel),
                            color = Gray400,
                            style = MulKkamTheme.typography.body4,
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CancelFriendsRequestDialogPreview() {
    MulkkamTheme {
        CancelFriendsRequestDialog(
            friendsRequest = FriendsRequestInfo(1L, Nickname("돈가스먹는환노")),
            onConfirm = {},
            onDismiss = {},
        )
    }
}
