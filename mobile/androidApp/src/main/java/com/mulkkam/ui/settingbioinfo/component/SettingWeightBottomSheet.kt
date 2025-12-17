package com.mulkkam.ui.settingbioinfo.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mulkkam.R
import com.mulkkam.domain.model.bio.BioWeight.Companion.WEIGHT_MAX
import com.mulkkam.domain.model.bio.BioWeight.Companion.WEIGHT_MIN
import com.mulkkam.ui.component.CustomNumberPicker
import com.mulkkam.ui.designsystem.Black
import com.mulkkam.ui.designsystem.Gray200
import com.mulkkam.ui.designsystem.Gray400
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.MulkkamTheme
import com.mulkkam.ui.designsystem.Primary200
import com.mulkkam.ui.designsystem.White
import com.mulkkam.ui.util.extensions.noRippleClickable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingWeightBottomSheet(
    initialWeight: Int,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onSave: (weight: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    var weight by rememberSaveable { mutableIntStateOf(initialWeight) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = modifier,
        sheetState = sheetState,
        dragHandle = {
            Column {
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier =
                        Modifier
                            .size(width = 68.dp, height = 4.dp)
                            .background(Gray400, shape = RoundedCornerShape(100.dp)),
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        },
        containerColor = White,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 24.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.weight_label),
                    style = MulKkamTheme.typography.title1,
                    color = Black,
                )

                Icon(
                    painter = painterResource(id = R.drawable.ic_bio_info_weight_close),
                    contentDescription = null,
                    tint = Gray400,
                    modifier =
                        Modifier
                            .size(40.dp)
                            .noRippleClickable(onClick = { onDismiss() })
                            .padding(12.dp),
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            CustomNumberPicker(
                range = IntRange(WEIGHT_MIN, WEIGHT_MAX),
                value = weight,
                onValueChange = { weight = it },
            )
            Spacer(modifier = Modifier.height(34.dp))
            Button(
                onClick = {
                    onSave(weight)
                    onDismiss()
                },
                colors =
                    ButtonColors(
                        containerColor = Primary200,
                        contentColor = White,
                        disabledContainerColor = Gray200,
                        disabledContentColor = White,
                    ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(0.dp),
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
            ) {
                Text(
                    text = stringResource(R.string.weight_complete),
                    style = MulKkamTheme.typography.title2,
                    color = White,
                    modifier = Modifier.padding(vertical = 12.dp),
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun SettingWeightBottomSheetPreview() {
    MulkkamTheme {
        SettingWeightBottomSheet(
            initialWeight = 60,
            onDismiss = {},
            onSave = {},
            sheetState = rememberStandardBottomSheetState(),
        )
    }
}
