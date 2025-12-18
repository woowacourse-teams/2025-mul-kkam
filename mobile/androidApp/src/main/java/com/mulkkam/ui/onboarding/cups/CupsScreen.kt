package com.mulkkam.ui.onboarding.cups

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mulkkam.R
import com.mulkkam.ui.component.MulKkamSnackbarHost
import com.mulkkam.ui.component.showMulKkamSnackbar
import com.mulkkam.ui.designsystem.Black
import com.mulkkam.ui.designsystem.Gray300
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.White
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.onboarding.component.NextButton
import com.mulkkam.ui.onboarding.component.OnboardingCompleteDialog
import com.mulkkam.ui.onboarding.component.OnboardingTopAppBar
import com.mulkkam.ui.onboarding.cups.component.CupBottomSheet
import com.mulkkam.ui.onboarding.cups.component.CupsEditor
import com.mulkkam.ui.settingcups.adapter.SettingCupsItem
import com.mulkkam.ui.settingcups.model.CupUiModel
import com.mulkkam.ui.settingcups.model.CupsUiModel
import com.mulkkam.ui.settingcups.model.SettingWaterCupEditType
import com.mulkkam.ui.util.extensions.collectWithLifecycle
import com.mulkkam.ui.util.extensions.getStyledText
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CupsScreen(
    navigateToBack: () -> Unit,
    navigateToCoffeeEncyclopedia: () -> Unit,
    currentProgress: Int,
    onCompleteOnboarding: () -> Unit,
    viewModel: CupsViewModel = koinViewModel(),
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
    var showDialog by remember { mutableStateOf(false) }

    var isBottomSheetVisible: Boolean by rememberSaveable { mutableStateOf(false) }
    val modalBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedCup: CupUiModel? by rememberSaveable { mutableStateOf(null) }

    val cupsUiState by viewModel.cupsUiState.collectAsStateWithLifecycle()
    val listItems = rememberSaveable { mutableStateListOf<SettingCupsItem>() }

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

    viewModel.saveOnboardingUiState.collectWithLifecycle(lifecycleOwner) { state ->
        when (state) {
            is MulKkamUiState.Success<Unit> -> showDialog = true
            is MulKkamUiState.Loading -> Unit
            is MulKkamUiState.Idle -> Unit
            is MulKkamUiState.Failure ->
                snackbarHostState.showMulKkamSnackbar(
                    message = context.getString(R.string.network_check_error),
                    iconResourceId = R.drawable.ic_alert_circle,
                )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
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
            if (showDialog) {
                OnboardingCompleteDialog(
                    nickname = viewModel.onboardingInfo.nickname?.name ?: "",
                    onConfirm = { onCompleteOnboarding() },
                )
            }

            if (isBottomSheetVisible) {
                CupBottomSheet(
                    sheetState = modalBottomSheetState,
                    initialCup = selectedCup,
                    onDismiss = { isBottomSheetVisible = false },
                    onAdd = { cup ->
                        viewModel.addCup(cup)
                        isBottomSheetVisible = false
                    },
                    onUpdate = { cup ->
                        viewModel.updateCup(cup)
                        isBottomSheetVisible = false
                    },
                    onDelete = { rank ->
                        viewModel.deleteCup(rank)
                        isBottomSheetVisible = false
                    },
                    onNavigateToCoffeeEncyclopedia = navigateToCoffeeEncyclopedia,
                    viewModel =
                        koinViewModel(
                            key =
                                selectedCup?.id?.toString()
                                    ?: (SettingWaterCupEditType.ADD.name + System.currentTimeMillis()),
                        ),
                )
            }

            Column(
                modifier =
                    Modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
            ) {
                Text(
                    text =
                        stringResource(
                            R.string.cups_input_hint,
                            viewModel.onboardingInfo.nickname?.name ?: "",
                        ).getStyledText(
                            highlightedText = arrayOf(stringResource(R.string.cups_input_hint_highlight)),
                            style = MulKkamTheme.typography.title1,
                        ),
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
                    onEditCup = { cup ->
                        selectedCup = cup
                        isBottomSheetVisible = true
                    },
                    onAddCup = {
                        selectedCup = null
                        isBottomSheetVisible = true
                    },
                    onReorderCups = { viewModel.updateCupOrder(it) },
                    modifier = Modifier,
                )

                Spacer(modifier = Modifier.weight(1f))

                NextButton(
                    onClick = { viewModel.completeOnboarding() },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(start = 24.dp, end = 24.dp, bottom = 24.dp),
                    buttonText = stringResource(R.string.onboarding_complete),
                )
            }
        }
        MulKkamSnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CupsScreenPreview() {
    MulKkamTheme {
        CupsScreen(
            navigateToBack = {},
            navigateToCoffeeEncyclopedia = {},
            currentProgress = 5,
            onCompleteOnboarding = {},
        )
    }
}
