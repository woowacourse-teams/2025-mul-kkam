package com.mulkkam.ui.pendingfriends.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mulkkam.R
import com.mulkkam.ui.designsystem.MulkkamTheme
import com.mulkkam.ui.designsystem.Secondary100
import com.mulkkam.ui.designsystem.Secondary200

@Composable
fun DeclineFriendButton(onClick: () -> Unit) {
    Box(
        modifier =
            Modifier
                .size(48.dp)
                .clickable { onClick }
                .padding(6.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Secondary100),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_pending_friends_decline),
            contentDescription = stringResource(R.string.pending_friends_decline_button_description),
            tint = Secondary200,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DeclineFriendButtonPreview() {
    MulkkamTheme {
        DeclineFriendButton(onClick = {})
    }
}
