package com.mulkkam.ui.setting.bioinfo.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.mulkkam.domain.model.bio.BioWeight
import com.mulkkam.ui.designsystem.Black
import com.mulkkam.ui.designsystem.Gray200
import com.mulkkam.ui.designsystem.Gray400
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.util.extensions.noRippleClickable
import mulkkam.shared.generated.resources.Res
import mulkkam.shared.generated.resources.bio_info_weight_format
import mulkkam.shared.generated.resources.bio_info_weight_hint
import mulkkam.shared.generated.resources.bio_info_weight_label
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun WeightSection(
    weight: BioWeight?,
    onClickSection: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        Text(
            text = stringResource(resource = Res.string.bio_info_weight_label),
            style = MulKkamTheme.typography.title2,
            color = Black,
        )

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier =
                Modifier
                    .height(46.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .noRippleClickable(onClick = { onClickSection() })
                    .border(
                        width = 1.dp,
                        color = Gray200,
                        shape = RoundedCornerShape(8.dp),
                    ).padding(vertical = 12.dp),
            contentAlignment = Alignment.Center,
        ) {
            val weightText =
                weight?.value?.let {
                    stringResource(resource = Res.string.bio_info_weight_format, it)
                } ?: stringResource(resource = Res.string.bio_info_weight_hint)

            Text(
                text = weightText,
                style = MulKkamTheme.typography.title3,
                color = Gray400,
            )
        }
    }
}

@Preview(showBackground = true, name = "몸무게 정보가 없을 때")
@Composable
private fun WeightSectionPreview_WithoutWeight() {
    MulKkamTheme {
        WeightSection(
            weight = null,
            onClickSection = {},
        )
    }
}

@Preview(showBackground = true, name = "몸무게 정보가 있을 때")
@Composable
private fun WeightSectionPreview_WithWeight() {
    MulKkamTheme {
        WeightSection(
            weight = BioWeight(50),
            onClickSection = {},
        )
    }
}
