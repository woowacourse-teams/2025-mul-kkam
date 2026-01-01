package com.mulkkam.ui.setting.setting.component

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mulkkam.ui.designsystem.Black
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.White
import mulkkam.shared.generated.resources.Res
import mulkkam.shared.generated.resources.ic_common_prev
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingTopAppBar(
    title: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {},
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                style = MulKkamTheme.typography.title1,
                color = Black,
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    painter = painterResource(Res.drawable.ic_common_prev),
                    contentDescription = "뒤로가기",
                    tint = Black,
                    modifier = Modifier.size(24.dp),
                )
            }
        },
        actions = actions,
        colors =
            TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = White,
            ),
        modifier = modifier.fillMaxWidth(),
    )
}
