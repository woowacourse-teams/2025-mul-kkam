package com.mulkkam.ui.settingbioinfo.component

import android.R.attr.text
import android.R.attr.top
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mulkkam.R
import com.mulkkam.domain.model.bio.Gender
import com.mulkkam.ui.designsystem.Black
import com.mulkkam.ui.designsystem.Gray400
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.MulkkamTheme
import com.mulkkam.ui.designsystem.Primary100
import com.mulkkam.ui.designsystem.White

@Composable
fun GenderSection(
    gender: Gender?,
    onClickGender: (Gender) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(
            text = stringResource(R.string.bio_info_gender_label),
            style = MulKkamTheme.typography.title2,
            color = Black,
        )

        Row(
            modifier = Modifier.padding(top = 24.dp),
        ) {
            GenderButton(
                text = stringResource(R.string.bio_info_gender_male),
                isSelected = gender == Gender.MALE,
                onClick = { onClickGender(Gender.MALE) },
                modifier = Modifier.weight(1f),
            )
            Spacer(modifier = Modifier.width(10.dp))
            GenderButton(
                text = stringResource(R.string.bio_info_gender_female),
                isSelected = gender == Gender.FEMALE,
                onClick = { onClickGender(Gender.FEMALE) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun GenderSectionPreview() {
    MulkkamTheme {
        GenderSection(gender = Gender.FEMALE, onClickGender = {})
    }
}
