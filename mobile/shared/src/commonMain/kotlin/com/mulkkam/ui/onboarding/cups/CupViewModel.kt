package com.mulkkam.ui.onboarding.cups

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.domain.model.IntakeType
import com.mulkkam.domain.model.cups.CupAmount
import com.mulkkam.domain.model.cups.CupName
import com.mulkkam.domain.model.result.toMulKkamError
import com.mulkkam.domain.repository.CupsRepository
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.model.toSuccessDataOrNull
import com.mulkkam.ui.setting.cups.model.CupEmojiUiModel.Companion.EMPTY_CUP_EMOJI_UI_MODEL
import com.mulkkam.ui.setting.cups.model.CupEmojisUiModel
import com.mulkkam.ui.setting.cups.model.CupUiModel
import com.mulkkam.ui.setting.cups.model.SettingWaterCupEditType
import com.mulkkam.ui.setting.cups.model.toUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CupViewModel(
    private val cupsRepository: CupsRepository,
) : ViewModel() {
    private val _cup: MutableStateFlow<CupUiModel> =
        MutableStateFlow(CupUiModel.EMPTY_CUP_UI_MODEL)
    val cup: StateFlow<CupUiModel> get() = _cup.asStateFlow()

    private val _editType: MutableStateFlow<SettingWaterCupEditType> =
        MutableStateFlow(SettingWaterCupEditType.ADD)
    val editType: StateFlow<SettingWaterCupEditType> get() = _editType.asStateFlow()

    private val _cupNameValidity: MutableStateFlow<MulKkamUiState<Unit>> =
        MutableStateFlow(MulKkamUiState.Idle)
    val cupNameValidity: StateFlow<MulKkamUiState<Unit>> get() = _cupNameValidity.asStateFlow()

    private val _amountValidity: MutableStateFlow<MulKkamUiState<Unit>> =
        MutableStateFlow(MulKkamUiState.Idle)
    val amountValidity: StateFlow<MulKkamUiState<Unit>> get() = _amountValidity.asStateFlow()

    private val _cupEmojisUiState: MutableStateFlow<MulKkamUiState<CupEmojisUiModel>> =
        MutableStateFlow(MulKkamUiState.Idle)
    val cupEmojisUiState: StateFlow<MulKkamUiState<CupEmojisUiModel>> get() = _cupEmojisUiState.asStateFlow()

    private val _actionInProgress: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val actionInProgress: StateFlow<Boolean> = _actionInProgress.asStateFlow()

    private var originalCup: CupUiModel = CupUiModel.EMPTY_CUP_UI_MODEL

    private val hasChanges: StateFlow<Boolean> =
        combine(_cup, _cupEmojisUiState) { cupValue, emojiState ->
            val emoji = emojiState.toSuccessDataOrNull()?.selectedCupEmoji
            (cupValue != originalCup) || (emoji != null)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false,
        )

    @Suppress("UNCHECKED_CAST")
    val isSaveAvailable: StateFlow<Boolean> =
        combine(
            listOf(
                _cup,
                _cupNameValidity,
                _amountValidity,
                _cupEmojisUiState,
                hasChanges,
                _editType,
                actionInProgress,
            ),
        ) { values ->

            val cupValue = values[0] as? CupUiModel ?: return@combine false
            val nameState = values[1] as? MulKkamUiState<Unit> ?: return@combine false
            val amountState = values[2] as? MulKkamUiState<Unit> ?: return@combine false
            val emojiState = values[3] as? MulKkamUiState<CupEmojisUiModel> ?: return@combine false
            val changed = values[4] as? Boolean ?: return@combine false
            val type = values[5] as? SettingWaterCupEditType ?: return@combine false

            if (!changed) return@combine false

            val isNameValid = nameState is MulKkamUiState.Success
            val isAmountValid = amountState is MulKkamUiState.Success

            val selectedEmoji = emojiState.toSuccessDataOrNull()?.selectedCupEmoji
            val isEmojiSelected = selectedEmoji != null
            val isEmojiChanged = selectedEmoji?.id != originalCup.emoji.id

            val isIntakeTypeChanged = cupValue.intakeType != originalCup.intakeType

            when (type) {
                SettingWaterCupEditType.ADD -> {
                    isNameValid && isAmountValid && isEmojiSelected
                }

                SettingWaterCupEditType.EDIT -> {
                    isNameValid || isAmountValid || isEmojiChanged || isIntakeTypeChanged
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false,
        )

    init {
        loadCupEmojis()
    }

    private fun loadCupEmojis() {
        if (cupEmojisUiState.value is MulKkamUiState.Success) return

        viewModelScope.launch {
            runCatching {
                _cupEmojisUiState.value = MulKkamUiState.Loading
                cupsRepository.getCupEmojis().getOrError()
            }.onSuccess {
                _cupEmojisUiState.value = MulKkamUiState.Success(it.toUi())
                when (editType.value) {
                    SettingWaterCupEditType.ADD -> {
                        selectEmoji(
                            it.firstOrNull()?.id ?: return@onSuccess,
                        )
                    }

                    SettingWaterCupEditType.EDIT -> {
                        selectEmoji(cup.value.emoji.id)
                    }
                }
            }
        }
    }

    fun initCup(cup: CupUiModel?) {
        val initialCup = cup ?: CupUiModel.EMPTY_CUP_UI_MODEL
        originalCup = initialCup
        _cup.value = initialCup
        _editType.value =
            if (cup == null) SettingWaterCupEditType.ADD else SettingWaterCupEditType.EDIT
        _cupNameValidity.value = MulKkamUiState.Idle
        _amountValidity.value = MulKkamUiState.Idle
    }

    fun updateCupName(name: String) {
        _cup.value = _cup.value.copy(name = name)

        if (name == originalCup.name) {
            _cupNameValidity.value = MulKkamUiState.Idle
            return
        }

        runCatching { CupName(name) }
            .onSuccess { _cupNameValidity.value = MulKkamUiState.Success(Unit) }
            .onFailure { _cupNameValidity.value = MulKkamUiState.Failure(it.toMulKkamError()) }
    }

    fun updateAmount(amount: Int) {
        _cup.value = _cup.value.copy(amount = amount)

        if (amount == originalCup.amount) {
            _amountValidity.value = MulKkamUiState.Idle
            return
        }

        runCatching { CupAmount(amount) }
            .onSuccess { _amountValidity.value = MulKkamUiState.Success(Unit) }
            .onFailure { _amountValidity.value = MulKkamUiState.Failure(it.toMulKkamError()) }
    }

    fun updateIntakeType(intakeType: IntakeType) {
        _cup.value = _cup.value.copy(intakeType = intakeType)
    }

    fun selectEmoji(emojiId: Long) {
        val emoji = cupEmojisUiState.value.toSuccessDataOrNull() ?: return
        _cupEmojisUiState.value = MulKkamUiState.Success(emoji.selectCupEmoji(emojiId))
        _cup.value =
            _cup.value.copy(
                emoji =
                    _cupEmojisUiState.value.toSuccessDataOrNull()?.selectedCupEmoji
                        ?: EMPTY_CUP_EMOJI_UI_MODEL,
            )
    }
}
