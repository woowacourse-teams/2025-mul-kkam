package com.mulkkam.ui.friends.component

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import com.mulkkam.R
import com.mulkkam.domain.model.friend.Friend
import com.mulkkam.ui.designsystem.Gray100
import com.mulkkam.ui.designsystem.Gray400
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.MulkkamTheme
import com.mulkkam.ui.designsystem.Primary100
import com.mulkkam.ui.designsystem.Secondary200
import com.mulkkam.ui.designsystem.White

@Composable
fun FriendDeleteConfirmationDialog(
    friend: Friend,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(White)
                    .padding(horizontal = 24.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_alert_circle),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = Secondary200,
                )
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.friends_delete_dialog_title, friend.nickname),
                style = MulKkamTheme.typography.title1,
                color = Gray400,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = stringResource(R.string.friends_delete_dialog_description),
                style = MulKkamTheme.typography.body2,
                color = Secondary200,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Button(
                    onClick = onConfirm,
                    modifier =
                        Modifier
                            .weight(1f)
                            .height(40.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = Primary100,
                            contentColor = White,
                        ),
                ) {
                    Text(
                        text = stringResource(R.string.friends_delete_dialog_confirm),
                        style = MulKkamTheme.typography.body4,
                    )
                }

                Button(
                    onClick = onDismiss,
                    modifier =
                        Modifier
                            .weight(1f)
                            .height(40.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = Gray100,
                            contentColor = Gray400,
                        ),
                ) {
                    Text(
                        text = stringResource(R.string.friends_delete_dialog_cancel),
                        style = MulKkamTheme.typography.body4,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FriendDeleteConfirmationDialogPreview() {
    MulkkamTheme {
        FriendDeleteConfirmationDialog(
            friend = Friend(id = 1, nickname = "공백"),
            onConfirm = {},
            onDismiss = {},
        )
    }
}
