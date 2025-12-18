package com.mulkkam.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.domain.model.cups.CupAmount
import com.mulkkam.domain.model.IntakeType
import com.mulkkam.domain.model.result.toMulKkamError
import com.mulkkam.ui.model.MulKkamUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ManualDrinkViewModel : ViewModel() {
    private val _amountValidity: MutableStateFlow<MulKkamUiState<Unit>> = MutableStateFlow(MulKkamUiState.Idle)
    val amountValidity: StateFlow<MulKkamUiState<Unit>> get() = _amountValidity.asStateFlow()

    private val _intakeType: MutableStateFlow<IntakeType> = MutableStateFlow(IntakeType.WATER)
    val intakeType: StateFlow<IntakeType> get() = _intakeType.asStateFlow()

    val isSaveAvailable: StateFlow<Boolean> =
        amountValidity
            .map { it is MulKkamUiState.Success }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    fun updateAmount(amount: Int?) {
        if (amount == null) {
            _amountValidity.value = MulKkamUiState.Idle
            return
        }

        runCatching {
            CupAmount(amount)
        }.onSuccess {
            _amountValidity.value = MulKkamUiState.Success(data = Unit)
        }.onFailure {
            _amountValidity.value = MulKkamUiState.Failure(error = it.toMulKkamError())
        }
    }

    fun updateIntakeType(intakeType: IntakeType) {
        _intakeType.value = intakeType
    }
}
