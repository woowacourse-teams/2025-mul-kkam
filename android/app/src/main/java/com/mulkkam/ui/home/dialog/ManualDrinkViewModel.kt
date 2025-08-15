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
    private val _amountValidity = MutableLiveData<MulKkamUiState<Unit>>(MulKkamUiState.Idle)
    val amountValidity: LiveData<MulKkamUiState<Unit>> get() = _amountValidity

    private val amountInput: MutableLiveData<Int> = MutableLiveData<Int>(0)
    private val intakeType: MutableLiveData<IntakeType> = MutableLiveData<IntakeType>(IntakeType.WATER)

    val isSaveAvailable: MediatorLiveData<Boolean> =
        MediatorLiveData<Boolean>().apply {
            fun update() {
                value = _amountValidity.value is MulKkamUiState.Success
            }
            addSource(_amountValidity) { update() }
            addSource(amountInput) { update() }
        }

    fun updateAmount(amount: Int) {
        amountInput.value = amount

        if (amount == 0) {
            _amountValidity.value = MulKkamUiState.Idle
            return
        }

        runCatching {
            CupCapacity(amount)
        }.onSuccess {
            _amountValidity.value = MulKkamUiState.Success(Unit)
        }.onFailure {
            _amountValidity.value = MulKkamUiState.Failure(it.toMulKkamError())
        }
    }

    fun updateIntakeType(type: IntakeType) {
        intakeType.value = type
    }
}
