package com.mulkkam.ui.settingcups

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mulkkam.R
import com.mulkkam.ui.designsystem.Gray100
import com.mulkkam.ui.designsystem.Gray400
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.Primary200
import com.mulkkam.ui.designsystem.White
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.settingcups.adapter.SettingCupsItem
import com.mulkkam.ui.settingcups.component.SETTING_CUPS_CUP_HEIGHT
import com.mulkkam.ui.settingcups.component.SettingCupsEditor
import com.mulkkam.ui.settingcups.model.CupUiModel
import com.mulkkam.ui.settingcups.model.CupsUiModel
import com.mulkkam.ui.util.extensions.noRippleClickable

@Composable
fun SettingCupsScreen(
    cupsUiState: MulKkamUiState<CupsUiModel>,
    items: SnapshotStateList<SettingCupsItem>,
    onResetClick: () -> Unit,
    onEditCup: (CupUiModel) -> Unit,
    onAddCup: () -> Unit,
    onReorderCups: (List<CupUiModel>) -> Unit,
    paddingValues: PaddingValues,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(White),
    ) {
        SettingCupsHeader(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp, top = 24.dp),
            onResetClick = onResetClick,
        )
        Spacer(modifier = Modifier.height(10.dp))
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f),
        ) {
            SettingCupsEditor(
                items = items,
                onEditCup = onEditCup,
                onAddCup = onAddCup,
                onReorderCups = onReorderCups,
                modifier = Modifier.fillMaxSize(),
            )
            if (cupsUiState is MulKkamUiState.Loading) {
                SettingCupsLoading(
                    modifier =
                        Modifier
                            .align(Alignment.TopCenter)
                            .padding(horizontal = 24.dp),
                )
            }
        }
    }
}

@Composable
private fun SettingCupsHeader(
    modifier: Modifier = Modifier,
    onResetClick: () -> Unit,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.setting_cups_cup_title),
            style = MulKkamTheme.typography.title2,
            color = Gray400,
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = stringResource(R.string.setting_cups_reset_default),
            style = MulKkamTheme.typography.body4,
            color = Primary200,
            modifier = Modifier.noRippleClickable(onClick = onResetClick),
        )
    }
}

@Composable
fun SettingCupsLoading(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        repeat(4) {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(SETTING_CUPS_CUP_HEIGHT)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Gray100),
            )
        }
    }
}
