package com.mulkkam.ui.setting.cups.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mulkkam.domain.model.cups.CupAmount
import com.mulkkam.domain.model.cups.CupName
import com.mulkkam.domain.model.result.MulKkamError
import com.mulkkam.ui.component.BottomSheetHandle
import com.mulkkam.ui.component.BottomSheetHeader
import com.mulkkam.ui.component.IntakeTypeChips
import com.mulkkam.ui.component.MulKkamTextField
import com.mulkkam.ui.component.MulKkamTextFieldState
import com.mulkkam.ui.component.SaveButton
import com.mulkkam.ui.component.showMulKkamSnackbar
import com.mulkkam.ui.designsystem.Gray400
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.White
import com.mulkkam.ui.home.home.component.BottomSheetSectionTitle
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.setting.cups.SettingCupViewModel
import com.mulkkam.ui.setting.cups.model.CupEmojisUiModel
import com.mulkkam.ui.setting.cups.model.CupUiModel
import com.mulkkam.ui.setting.cups.model.SettingWaterCupEditType
import com.mulkkam.ui.util.extensions.collectWithLifecycle
import com.mulkkam.ui.util.extensions.sanitizeLeadingZeros
import mulkkam.shared.generated.resources.Res
import mulkkam.shared.generated.resources.ic_terms_all_check_on
import mulkkam.shared.generated.resources.setting_cup_add_title
import mulkkam.shared.generated.resources.setting_cup_amount
import mulkkam.shared.generated.resources.setting_cup_delete_result
import mulkkam.shared.generated.resources.setting_cup_edit_title
import mulkkam.shared.generated.resources.setting_cup_emoji
import mulkkam.shared.generated.resources.setting_cup_intake_type
import mulkkam.shared.generated.resources.setting_cup_invalid_range
import mulkkam.shared.generated.resources.setting_cup_name_invalid_characters
import mulkkam.shared.generated.resources.setting_cup_name_invalid_range
import mulkkam.shared.generated.resources.setting_cup_nickname
import mulkkam.shared.generated.resources.setting_cup_save
import mulkkam.shared.generated.resources.setting_cup_save_result
import mulkkam.shared.generated.resources.setting_target_amount_unit_ml
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingCupBottomSheet(
    sheetState: SheetState,
    initialCup: CupUiModel?,
    onDismiss: () -> Unit,
    onSaved: () -> Unit,
    onDeleted: () -> Unit,
    onNavigateToCoffeeEncyclopedia: () -> Unit,
    snackbarHostState: SnackbarHostState,
    viewModel: SettingCupViewModel,
    modifier: Modifier = Modifier,
) {
    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
    val focusManager: FocusManager = LocalFocusManager.current

    val cup: CupUiModel by viewModel.cup.collectAsStateWithLifecycle(initialValue = CupUiModel.EMPTY_CUP_UI_MODEL)
    val editType: SettingWaterCupEditType by viewModel.editType.collectAsStateWithLifecycle(initialValue = SettingWaterCupEditType.ADD)
    val cupNameValidity: MulKkamUiState<Unit> by viewModel.cupNameValidity.collectAsStateWithLifecycle(initialValue = MulKkamUiState.Idle)
    val amountValidity: MulKkamUiState<Unit> by viewModel.amountValidity.collectAsStateWithLifecycle(initialValue = MulKkamUiState.Idle)
    val cupEmojisUiState: MulKkamUiState<CupEmojisUiModel> by viewModel.cupEmojisUiState.collectAsStateWithLifecycle(
        initialValue = MulKkamUiState.Idle,
    )
    val isSaveAvailable: Boolean by viewModel.isSaveAvailable.collectAsStateWithLifecycle(initialValue = false)

    var cupNameText by rememberSaveable { mutableStateOf(cup.name) }
    var cupAmountText by rememberSaveable { mutableStateOf(if (cup.amount == 0) "" else cup.amount.toString()) }

    LaunchedEffect(initialCup) {
        viewModel.initCup(initialCup)
    }

    LaunchedEffect(cup.id, cup.name, cup.amount) {
        cupNameText = cup.name
        cupAmountText = if (cup.amount == 0) "" else cup.amount.toString()
    }

    viewModel.saveSuccess.collectWithLifecycle(lifecycleOwner) {
        snackbarHostState.showMulKkamSnackbar(
            message = getString(Res.string.setting_cup_save_result),
            iconResource = Res.drawable.ic_terms_all_check_on,
        )
        onSaved()
        onDismiss()
    }

    viewModel.deleteSuccess.collectWithLifecycle(lifecycleOwner) {
        snackbarHostState.showMulKkamSnackbar(
            message = getString(Res.string.setting_cup_delete_result),
            iconResource = Res.drawable.ic_terms_all_check_on,
        )
        onDeleted()
        onDismiss()
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = modifier,
        shape =
            RoundedCornerShape(
                topStart = 12.dp,
                topEnd = 12.dp,
            ),
        sheetState = sheetState,
        dragHandle = { BottomSheetHandle() },
        containerColor = White,
    ) {
        Column(
            modifier =
                Modifier
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 24.dp)
                    .padding(bottom = 24.dp)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                    ) {
                        focusManager.clearFocus()
                    },
        ) {
            BottomSheetHeader(
                title =
                    when (editType) {
                        SettingWaterCupEditType.ADD -> stringResource(Res.string.setting_cup_add_title)
                        SettingWaterCupEditType.EDIT -> stringResource(Res.string.setting_cup_edit_title)
                    },
                onDismiss = onDismiss,
            )

            Spacer(modifier = Modifier.height(18.dp))
            BottomSheetSectionTitle(title = stringResource(Res.string.setting_cup_emoji))
            Spacer(modifier = Modifier.height(12.dp))
            EmojiSection(
                cupEmojisUiState = cupEmojisUiState,
                onSelect = viewModel::selectEmoji,
            )

            Spacer(modifier = Modifier.height(18.dp))
            BottomSheetSectionTitle(title = stringResource(Res.string.setting_cup_nickname))
            Spacer(modifier = Modifier.height(10.dp))
            MulKkamTextField(
                value = cupNameText,
                onValueChanged = { newValue ->
                    cupNameText = newValue
                    viewModel.updateCupName(newValue)
                },
                maxLength = CupName.CUP_NAME_LENGTH_MAX,
                state = cupNameValidity.toTextFieldState(),
            )
            Spacer(modifier = Modifier.height(8.dp))
            ValidationMessage(
                message = cupNameValidity.toCupNameMessage(),
            )

            Spacer(modifier = Modifier.height(18.dp))
            BottomSheetSectionTitle(
                title = stringResource(Res.string.setting_cup_intake_type),
                onClickInfo = onNavigateToCoffeeEncyclopedia,
            )
            Spacer(modifier = Modifier.height(4.dp))
            IntakeTypeChips(
                selectedIntakeType = cup.intakeType,
                onSelect = viewModel::updateIntakeType,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(12.dp))
            BottomSheetSectionTitle(title = stringResource(Res.string.setting_cup_amount))
            Spacer(modifier = Modifier.height(10.dp))
            MulKkamTextField(
                value = cupAmountText,
                onValueChanged = { newValue ->
                    if (newValue.all(Char::isDigit)) {
                        val sanitized = newValue.sanitizeLeadingZeros()
                        cupAmountText = sanitized
                        viewModel.updateAmount(sanitized.toIntOrNull() ?: 0)
                    }
                },
                maxLength = CupAmount.MAX_ML.toString().length,
                suffix = { suffixModifier ->
                    Text(
                        text = stringResource(Res.string.setting_target_amount_unit_ml),
                        style = MulKkamTheme.typography.title2,
                        color = Gray400,
                        modifier = suffixModifier,
                    )
                },
                state = amountValidity.toTextFieldState(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
            Spacer(modifier = Modifier.height(6.dp))
            ValidationMessage(message = amountValidity.toCupAmountMessage())

            Spacer(modifier = Modifier.height(22.dp))
            SaveButton(
                onClick = viewModel::saveCup,
                enabled = isSaveAvailable,
                text = stringResource(Res.string.setting_cup_save),
            )

            if (editType == SettingWaterCupEditType.EDIT && !cup.isRepresentative) {
                Spacer(modifier = Modifier.height(10.dp))
                DeleteCupButton(
                    onClick = viewModel::deleteCup,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                )
            }
        }
    }
}

private fun MulKkamUiState<Unit>.toTextFieldState(): MulKkamTextFieldState =
    when (this) {
        is MulKkamUiState.Success -> MulKkamTextFieldState.VALID
        is MulKkamUiState.Failure -> MulKkamTextFieldState.ERROR
        else -> MulKkamTextFieldState.NORMAL
    }

@Composable
private fun MulKkamUiState<Unit>.toCupNameMessage(): String =
    when (this) {
        is MulKkamUiState.Failure -> {
            when (error) {
                is MulKkamError.SettingCupsError.InvalidNicknameLength -> {
                    stringResource(
                        Res.string.setting_cup_name_invalid_range,
                        CupName.CUP_NAME_LENGTH_MIN,
                        CupName.CUP_NAME_LENGTH_MAX,
                    )
                }

                is MulKkamError.SettingCupsError.InvalidNicknameCharacters -> {
                    stringResource(Res.string.setting_cup_name_invalid_characters)
                }

                else -> {
                    ""
                }
            }
        }

        else -> {
            ""
        }
    }

@Composable
private fun MulKkamUiState<Unit>.toCupAmountMessage(): String =
    when (this) {
        is MulKkamUiState.Failure -> {
            if (error is MulKkamError.SettingCupsError.InvalidAmount) {
                stringResource(Res.string.setting_cup_invalid_range, CupAmount.MIN_ML, CupAmount.MAX_ML)
            } else {
                ""
            }
        }

        else -> {
            ""
        }
    }
