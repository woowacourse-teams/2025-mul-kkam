
package com.mulkkam.ui.pendingfriends.component

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mulkkam.domain.model.friends.PendingFriend
import com.mulkkam.ui.designsystem.Gray100

@Composable
fun SentTab(
    sentRequests: List<PendingFriend>,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier.fillMaxHeight()) {
        items(
            sentRequests.size,
            key = { sentRequests[it].name },
        ) { index ->
            SentRequestItem(
                pendingFriend = sentRequests[index],
                onCancel = { onCancel },
            )

            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 1.dp,
                color = Gray100,
            )
        }
    }
}
