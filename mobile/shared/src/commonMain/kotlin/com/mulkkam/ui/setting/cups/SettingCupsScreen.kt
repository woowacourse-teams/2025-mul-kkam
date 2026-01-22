package com.mulkkam.ui.setting.cups

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.mulkkam.domain.model.IntakeType
import com.mulkkam.ui.designsystem.Gray100
import com.mulkkam.ui.designsystem.Gray300
import com.mulkkam.ui.designsystem.Gray400
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.White
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.setting.cups.adapter.SettingCupsItem
import com.mulkkam.ui.setting.cups.component.SETTING_CUPS_CUP_HEIGHT
import com.mulkkam.ui.setting.cups.component.SettingCupsEditor
import com.mulkkam.ui.setting.cups.component.SettingCupsTopAppBar
import com.mulkkam.ui.setting.cups.model.CupEmojiUiModel
import com.mulkkam.ui.setting.cups.model.CupUiModel
import com.mulkkam.ui.setting.cups.model.CupsUiModel
import com.mulkkam.ui.util.extensions.noRippleClickable
import mulkkam.shared.generated.resources.Res
import mulkkam.shared.generated.resources.setting_cups_cup_title
import mulkkam.shared.generated.resources.setting_cups_reset_default
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun SettingCupsScreen(
    padding: PaddingValues,
    cupsUiState: MulKkamUiState<CupsUiModel>,
    items: SnapshotStateList<SettingCupsItem>,
    onResetClick: () -> Unit,
    onEditCup: (CupUiModel) -> Unit,
    onAddCup: () -> Unit,
    onReorderCups: (List<CupUiModel>) -> Unit,
    onBackClick: () -> Unit,
) {
    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        topBar = { SettingCupsTopAppBar(onBackClick = onBackClick) },
        containerColor = White,
        modifier = Modifier.fillMaxSize().padding(padding),
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(top = paddingValues.calculateTopPadding())
                    .background(White),
        ) {
            SettingCupsHeader(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, end = 12.dp, top = 24.dp),
                onResetClick = onResetClick,
            )
            Spacer(modifier = Modifier.height(2.dp))
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
            text = stringResource(Res.string.setting_cups_cup_title),
            style = MulKkamTheme.typography.title2,
            color = Gray400,
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = stringResource(Res.string.setting_cups_reset_default),
            style = MulKkamTheme.typography.label2,
            color = Gray300,
            modifier =
                Modifier
                    .padding(12.dp)
                    .noRippleClickable(onClick = onResetClick),
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

@Preview(showBackground = true)
@Composable
private fun SettingCupsScreenPreview() {
    MulKkamTheme {
        val previewItems =
            remember {
                mutableStateListOf<SettingCupsItem>().apply {
                    addAll(previewCupItems().map { cup -> SettingCupsItem.CupItem(cup) })
                    add(SettingCupsItem.AddItem)
                }
            }
        SettingCupsScreen(
            cupsUiState = MulKkamUiState.Success(CupsUiModel(previewCupItems(), isAddable = true)),
            padding = PaddingValues(),
            items = previewItems,
            onResetClick = {},
            onEditCup = {},
            onAddCup = {},
            onReorderCups = {},
            onBackClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingCupsLoadingPreview() {
    MulKkamTheme {
        SettingCupsLoading(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
        )
    }
}

private fun previewCupItems(): List<CupUiModel> =
    listOf(
        CupUiModel(
            id = 1L,
            name = "대표 컵",
            amount = 300,
            rank = 1,
            intakeType = IntakeType.WATER,
            emoji = CupEmojiUiModel(id = 1L, cupEmojiUrl = ""),
            isRepresentative = true,
        ),
        CupUiModel(
            id = 2L,
            name = "커피 컵",
            amount = 200,
            rank = 2,
            intakeType = IntakeType.COFFEE,
            emoji = CupEmojiUiModel(id = 2L, cupEmojiUrl = ""),
            isRepresentative = false,
        ),
    )
