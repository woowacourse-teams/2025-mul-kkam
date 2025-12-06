package com.mulkkam.ui.settingaccountinfo.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mulkkam.R
import com.mulkkam.ui.designsystem.Gray400
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.MulkkamTheme

@Composable
fun SettingAccountInfoTopAppBar(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                painter = painterResource(R.drawable.ic_common_prev),
                contentDescription = null,
                tint = Gray400,
            )
        }
        Text(
            text = stringResource(R.string.setting_account_info),
            style = MulKkamTheme.typography.title2,
            color = Gray400,
        )
    }
}

@Preview
@Composable
private fun SettingAccountInfoTopAppBarPreview() {
    MulkkamTheme {
        SettingAccountInfoTopAppBar(onBackClick = {})
    }
}
