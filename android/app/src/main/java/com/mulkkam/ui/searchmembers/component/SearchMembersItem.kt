package com.mulkkam.ui.searchmembers.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mulkkam.R
import com.mulkkam.ui.designsystem.Black
import com.mulkkam.ui.designsystem.Gray100
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.MulkkamTheme
import com.mulkkam.ui.designsystem.Primary100
import com.mulkkam.ui.designsystem.White
import com.mulkkam.ui.util.extensions.noRippleClickable

@Composable
fun SearchMembersItem(
    name: String,
    onRequest: () -> Unit,
) {
    Column {
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = Gray100,
        )

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 16.dp, top = 12.dp, bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = name,
                color = Black,
                style = MulKkamTheme.typography.title1,
            )

            Box(
                modifier =
                    Modifier
                        .size(48.dp)
                        .noRippleClickable(onClick = onRequest)
                        .padding(6.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Primary100),
            ) {
                Icon(
                    modifier =
                        Modifier
                            .align(Alignment.Center)
                            .fillMaxSize(),
                    painter = painterResource(R.drawable.ic_setting_add),
                    contentDescription = null,
                    tint = White,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchMembersItemPreview() {
    MulkkamTheme {
        SearchMembersItem(name = "돈가스먹는환노", onRequest = {})
    }
}
