package com.mulkkam.ui.home.dialog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mulkkam.domain.model.cups.CupCapacity
import com.mulkkam.domain.model.intake.IntakeType
import com.mulkkam.domain.model.result.toMulKkamError
import com.mulkkam.ui.model.MulKkamUiState

class ManualDrinkViewModel : ViewModel() {
    private val _amountInput = MutableLiveData<Int>(0)
    val amountInput: LiveData<Int> get() = _amountInput

    private val _amountValidity = MutableLiveData<MulKkamUiState<Unit>>(MulKkamUiState.Idle)
    val amountValidity: LiveData<MulKkamUiState<Unit>> get() = _amountValidity

    private val _intakeType = MutableLiveData<IntakeType>(IntakeType.WATER)
    val intakeType: LiveData<IntakeType> get() = _intakeType

    val isSaveAvailable: MediatorLiveData<Boolean> =
        MediatorLiveData<Boolean>().apply {
            fun update() {
                value = _amountValidity.value is MulKkamUiState.Success
            }
            addSource(_amountValidity) { update() }
            addSource(_amountInput) { update() }
        }

    fun updateAmount(amount: Int) {
        _amountInput.value = amount

        runCatching {
            CupCapacity(amount)
        }.onSuccess {
            _amountValidity.value = MulKkamUiState.Success(Unit)
        }.onFailure {
            _amountValidity.value = MulKkamUiState.Failure(it.toMulKkamError())
        }
    }

    fun updateIntakeType(type: IntakeType) {
        _intakeType.value = type
    }
}
