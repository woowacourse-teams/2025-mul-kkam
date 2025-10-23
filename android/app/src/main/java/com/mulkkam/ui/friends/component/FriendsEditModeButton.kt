package com.mulkkam.ui.friends.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.mulkkam.R
import com.mulkkam.ui.designsystem.Gray300
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.friends.model.FriendsDisplayMode
import com.mulkkam.ui.util.extensions.noRippleClickable

@Composable
fun FriendsEditModeButton(
    displayMode: FriendsDisplayMode,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val buttonTextResId: Int =
        when (displayMode) {
            FriendsDisplayMode.VIEWING -> R.string.friends_edit_button
            FriendsDisplayMode.EDITING -> R.string.friends_edit_cancel_button
        }

    Text(
        text = stringResource(buttonTextResId),
        modifier = modifier.noRippleClickable(onClick = onClick),
        style = MulKkamTheme.typography.label1,
        color = Gray300,
    )
}
