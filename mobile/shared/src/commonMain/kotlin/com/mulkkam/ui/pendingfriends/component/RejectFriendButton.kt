package com.mulkkam.ui.pendingfriends.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.Secondary100
import com.mulkkam.ui.designsystem.Secondary200
import com.mulkkam.ui.util.extensions.noRippleClickable
import mulkkam.shared.generated.resources.Res
import mulkkam.shared.generated.resources.ic_pending_friends_reject
import mulkkam.shared.generated.resources.pending_friends_reject_button_description
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun RejectFriendButton(onClick: () -> Unit) {
    Box(
        modifier =
            Modifier
                .size(48.dp)
                .noRippleClickable(onClick = onClick)
                .padding(6.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Secondary100),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_pending_friends_reject),
            contentDescription = stringResource(Res.string.pending_friends_reject_button_description),
            tint = Secondary200,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RejectFriendButtonPreview() {
    MulKkamTheme {
        RejectFriendButton(onClick = {})
    }
}
