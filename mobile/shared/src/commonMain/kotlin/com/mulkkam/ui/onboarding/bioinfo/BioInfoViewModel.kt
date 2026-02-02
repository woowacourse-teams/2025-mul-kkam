package com.mulkkam.ui.onboarding.bioinfo

import androidx.lifecycle.ViewModel
import com.mulkkam.domain.model.Gender
import com.mulkkam.domain.model.bio.BioWeight
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class BioInfoViewModel : ViewModel() {
    private val _gender: MutableStateFlow<Gender?> = MutableStateFlow(null)
    val gender: StateFlow<Gender?>
        get() = _gender.asStateFlow()

    private val _weight: MutableStateFlow<BioWeight?> = MutableStateFlow(null)
    val weight: StateFlow<BioWeight?>
        get() = _weight.asStateFlow()

    fun updateWeight(value: Int) {
        runCatching {
            BioWeight(value)
        }.onSuccess {
            _weight.value = it
        }.onFailure {
            _weight.value = BioWeight()
        }
    }

    fun updateGender(gender: Gender) {
        _gender.value = gender
    }

    fun clearBioInfo() {
        _gender.value = null
        _weight.value = null
    }
}
