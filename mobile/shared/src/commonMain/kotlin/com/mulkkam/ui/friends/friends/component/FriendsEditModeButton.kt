package com.mulkkam.ui.friends.friends.component

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
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun FriendsEditModeButton(
    displayMode: FriendsDisplayMode,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val buttonTextRes: StringResource =
        when (displayMode) {
            FriendsDisplayMode.VIEWING -> Res.string.friends_edit_button
            FriendsDisplayMode.EDITING -> Res.string.friends_edit_cancel_button
        }

    Text(
        text = stringResource(buttonTextRes),
        modifier = modifier.noRippleClickable(onClick = onClick),
        style = MulKkamTheme.typography.label1,
        color = Gray300,
    )
}
