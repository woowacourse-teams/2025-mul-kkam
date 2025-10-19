package com.mulkkam.ui.setting.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mulkkam.R
import com.mulkkam.ui.designsystem.MulKkamTheme.typography
import com.mulkkam.ui.designsystem.MulkkamTheme
import com.mulkkam.ui.setting.adapter.SettingItem
import com.mulkkam.ui.setting.model.SettingType

@Composable
fun SettingNormalItem(
    item: SettingItem.NormalItem,
    onSettingClick: (SettingType) -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable { onSettingClick(item.type) },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = item.label,
            style = typography.body2,
            color = Color.Black,
            modifier = Modifier.padding(start = 24.dp, top = 14.dp, bottom = 14.dp),
        )

        Image(
            painter = painterResource(id = R.drawable.ic_setting_next),
            contentDescription = null,
            modifier =
                Modifier
                    .size(40.dp)
                    .padding(8.dp)
                    .align(Alignment.CenterVertically),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSettingNormalItem() {
    MulkkamTheme {
        SettingNormalItem(
            item =
                SettingItem.NormalItem(
                    label = "닉네임 변경",
                    type = SettingType.NICKNAME,
                ),
            onSettingClick = {},
        )
    }
}
