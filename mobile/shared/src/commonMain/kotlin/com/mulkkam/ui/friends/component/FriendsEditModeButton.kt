package com.mulkkam.ui.friends.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mulkkam.ui.designsystem.Gray300
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.friends.model.FriendsDisplayMode
import com.mulkkam.ui.util.extensions.noRippleClickable
import mulkkam.shared.generated.resources.Res
import mulkkam.shared.generated.resources.friends_edit_button
import mulkkam.shared.generated.resources.friends_edit_cancel_button
import org.jetbrains.compose.resources.stringResource

@Composable
fun FriendsEditModeButton(
    displayMode: FriendsDisplayMode,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val buttonText: String =
        when (displayMode) {
            FriendsDisplayMode.VIEWING -> stringResource(Res.string.friends_edit_button)
            FriendsDisplayMode.EDITING -> stringResource(Res.string.friends_edit_cancel_button)
        }

    Text(
        text = buttonText,
        modifier = modifier.noRippleClickable(onClick = onClick),
        style = MulKkamTheme.typography.label1,
        color = Gray300,
    )
}
