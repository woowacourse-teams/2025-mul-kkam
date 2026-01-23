package com.mulkkam.ui.onboarding.cups

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mulkkam.domain.model.OnboardingInfo
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
import com.mulkkam.ui.setting.cups.adapter.SettingCupsItem
import com.mulkkam.ui.setting.cups.model.CupUiModel
import com.mulkkam.ui.setting.cups.model.CupsUiModel
import com.mulkkam.ui.setting.cups.model.WaterCupEditType
import com.mulkkam.ui.util.extensions.collectWithLifecycle
import com.mulkkam.ui.util.extensions.getStyledText
import mulkkam.shared.generated.resources.Res
import mulkkam.shared.generated.resources.cups_input_hint
import mulkkam.shared.generated.resources.cups_input_hint_description
import mulkkam.shared.generated.resources.cups_input_hint_highlight
import mulkkam.shared.generated.resources.ic_alert_circle
import mulkkam.shared.generated.resources.network_check_error
import mulkkam.shared.generated.resources.onboarding_complete
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun CupsScreen(
    padding: PaddingValues,
    onboardingInfo: OnboardingInfo,
    navigateToBack: () -> Unit,
    navigateToCoffeeEncyclopedia: () -> Unit,
    currentProgress: Int,
    onCompleteOnboarding: () -> Unit,
    snackbarHostState: SnackbarHostState,
    viewModel: CupsViewModel = koinViewModel(),
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    var showDialog by remember { mutableStateOf(false) }

    var isBottomSheetVisible: Boolean by rememberSaveable { mutableStateOf(false) }
    val modalBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedCup: CupUiModel? by remember { mutableStateOf(null) }

    val cupsUiState by viewModel.cupsUiState.collectAsStateWithLifecycle()
    val listItems = remember { mutableStateListOf<SettingCupsItem>() }

    LaunchedEffect(Unit) {
        viewModel.updateOnboardingInfo(onboardingInfo = onboardingInfo)
    }

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
                    message = getString(resource = Res.string.network_check_error),
                    iconResource = Res.drawable.ic_alert_circle,
                )
        }
    }

    Scaffold(
        topBar = {
            OnboardingTopAppBar(
                onBackClick = navigateToBack,
                currentProgress = currentProgress,
            )
        },
        containerColor = White,
        modifier =
            Modifier
                .background(White)
                .padding(padding),
    ) { innerPadding ->
        if (showDialog) {
            OnboardingCompleteDialog(
                nickname = onboardingInfo.nickname?.name ?: "",
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
                                ?: (WaterCupEditType.ADD.name + Clock.System.now()),
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
                        resource = Res.string.cups_input_hint,
                        onboardingInfo.nickname?.name ?: "",
                    ).getStyledText(
                        style = MulKkamTheme.typography.title1,
                        stringResource(resource = Res.string.cups_input_hint_highlight),
                    ),
                style = MulKkamTheme.typography.body1,
                color = Black,
                modifier = Modifier.padding(top = 24.dp, start = 24.dp),
            )

            Text(
                text = stringResource(resource = Res.string.cups_input_hint_description),
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
                buttonText = stringResource(resource = Res.string.onboarding_complete),
            )
        }
    }
}
