package com.mulkkam.ui.onboarding.cups

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mulkkam.R
import com.mulkkam.ui.component.StyledText
import com.mulkkam.ui.designsystem.Black
import com.mulkkam.ui.designsystem.Gray300
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.MulkkamTheme
import com.mulkkam.ui.designsystem.White
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.onboarding.component.NextButton
import com.mulkkam.ui.onboarding.component.OnboardingTopAppBar
import com.mulkkam.ui.onboarding.cups.component.CupsEditor
import com.mulkkam.ui.settingcups.adapter.SettingCupsItem
import com.mulkkam.ui.settingcups.model.CupUiModel
import com.mulkkam.ui.settingcups.model.CupsUiModel

@Composable
fun CupsScreen(
    navigateToBack: () -> Unit,
    currentProgress: Int,
    nickname: String,
    onEditCup: (CupUiModel) -> Unit = {},
    onAddCup: () -> Unit = {},
    viewModel: CupsViewModel = hiltViewModel(),
) {
    val cupsUiState by viewModel.cupsUiState.collectAsStateWithLifecycle()
    val listItems = remember { mutableStateListOf<SettingCupsItem>() }

    LaunchedEffect(cupsUiState) {
        if (cupsUiState is MulKkamUiState.Success<CupsUiModel>) {
            val data =
                (cupsUiState as? MulKkamUiState.Success<CupsUiModel>)?.data ?: return@LaunchedEffect
            val cupItems: List<SettingCupsItem> =
                buildList {
                    addAll(data.cups.map { cup -> SettingCupsItem.CupItem(cup) })
                    if (data.isAddable) add(SettingCupsItem.AddItem)
                }
            listItems.clear()
            listItems.addAll(cupItems)
        }
    }

    Scaffold(
        topBar = {
            OnboardingTopAppBar(
                onBackClick = { navigateToBack() },
                currentProgress = currentProgress,
            )
        },
        containerColor = White,
        modifier =
            Modifier
                .background(White)
                .systemBarsPadding(),
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
        ) {
            StyledText(
                fullText = stringResource(R.string.cups_input_hint, nickname),
                highlightedTexts = listOf(stringResource(R.string.cups_input_hint_highlight)),
                highlightStyle = MulKkamTheme.typography.title1,
                style = MulKkamTheme.typography.body1,
                color = Black,
                modifier = Modifier.padding(top = 24.dp, start = 24.dp),
            )

            Text(
                text = stringResource(R.string.cups_input_hint_description),
                style = MulKkamTheme.typography.label2,
                color = Gray300,
                modifier = Modifier.padding(top = 8.dp, start = 24.dp),
            )

            Spacer(modifier = Modifier.height(10.dp))

            CupsEditor(
                items = listItems,
                onEditCup = onEditCup,
                onAddCup = onAddCup,
                onReorderCups = { viewModel.updateCupOrder(it) },
                modifier = Modifier,
            )

            Spacer(modifier = Modifier.weight(1f))

            NextButton(
                onClick = { },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, end = 24.dp, bottom = 24.dp),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CupsScreenPreview() {
    MulkkamTheme {
        CupsScreen(
            navigateToBack = {},
            currentProgress = 5,
            nickname = "돈가스먹는환노",
        )
    }
}
