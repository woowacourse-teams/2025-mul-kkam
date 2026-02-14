package com.mulkkam.ui.component

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.mulkkam.ui.designsystem.Gray400
import com.mulkkam.ui.designsystem.Gray50
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.Primary100
import com.mulkkam.ui.designsystem.Primary200
import com.mulkkam.ui.designsystem.White
import com.mulkkam.ui.util.extensions.noRippleClickable
import mulkkam.shared.generated.resources.Res
import mulkkam.shared.generated.resources.ic_info_circle
import mulkkam.shared.generated.resources.pending_friends_cancel_request_cancel
import mulkkam.shared.generated.resources.pending_friends_cancel_request_confirm
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun MulKkamInfoDialog(
    title: String,
    description: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
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
            modifier = modifier,
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
                    painter = painterResource(Res.drawable.ic_info_circle),
                    contentDescription = null,
                    tint = Primary100,
                    modifier = Modifier.size(48.dp),
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = title,
                    color = Gray400,
                    style = MulKkamTheme.typography.title1,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    color = Primary200,
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
                            text = stringResource(Res.string.pending_friends_cancel_request_confirm),
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
                            text = stringResource(Res.string.pending_friends_cancel_request_cancel),
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
private fun MulKkamInfoDialogPreview() {
    MulKkamTheme {
        MulKkamInfoDialog(
            title = "돈가스먹는환노\n친구 신청을 수락하시겠습니까?",
            description = "친구에게 물풍선을 던져 보세요",
            onConfirm = {},
            onDismiss = {},
        )
    }
}
