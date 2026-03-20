package com.mulkkam.ui.setting.bioinfo.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mulkkam.ui.designsystem.Black
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.util.extensions.noRippleClickable
import mulkkam.shared.generated.resources.Res
import mulkkam.shared.generated.resources.ic_common_next
import mulkkam.shared.generated.resources.setting_bio_info_health_kit_description
import mulkkam.shared.generated.resources.setting_bio_info_health_kit_label
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
actual fun HealthSection(
    onClick: () -> Unit,
    modifier: Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(resource = Res.string.setting_bio_info_health_kit_label),
            style = MulKkamTheme.typography.title2,
            color = Black,
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier =
                Modifier
                    .noRippleClickable(onClick = { onClick() })
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(resource = Res.string.setting_bio_info_health_kit_description),
                style = MulKkamTheme.typography.body2,
                color = Black,
            )

            Icon(
                modifier = Modifier.size(40.dp).padding(4.dp),
                painter = painterResource(resource = Res.drawable.ic_common_next),
                contentDescription = null,
            )
        }
    }
}
