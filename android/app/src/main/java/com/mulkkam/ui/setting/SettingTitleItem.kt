package com.mulkkam.ui.setting

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
import com.mulkkam.ui.designsystem.MulKkamTheme.typography
import com.mulkkam.ui.designsystem.MulkkamTheme
import com.mulkkam.ui.setting.adapter.SettingItem

@Composable
fun SettingTitleItem(item: SettingItem.TitleItem) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, top = 12.dp),
    ) {
        Text(
            text = item.title,
            style = typography.label1,
            color = Color.Black,
        )
        HorizontalDivider(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
            thickness = 1.dp,
            color = Gray50,
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
