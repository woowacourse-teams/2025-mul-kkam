package com.mulkkam.ui.setting.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mulkkam.ui.designsystem.Gray50
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.MulkkamTheme
import com.mulkkam.ui.setting.adapter.SettingItem

@Composable
fun SettingTitleItem(item: SettingItem.TitleItem) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth(),
    ) {
        Text(
            text = item.title,
            style = MulKkamTheme.typography.label1,
            color = Color.Black,
            modifier = Modifier.padding(start = 24.dp, top = 12.dp, bottom = 8.dp),
        )
        HorizontalDivider(
            color = Gray50,
            thickness = 1.dp,
            modifier =
                Modifier
                    .fillMaxWidth(),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSettingTitleItem() {
    MulkkamTheme {
        SettingTitleItem(
            item = SettingItem.TitleItem("계정 정보 설정"),
        )
    }
}
