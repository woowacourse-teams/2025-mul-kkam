package com.mulkkam.ui.settingbioinfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mulkkam.domain.Gender

class SettingBioInfoViewModel : ViewModel() {
    private val _gender = MutableLiveData<Gender>()
    val gender: LiveData<Gender>
        get() = _gender

    private val _weight = MutableLiveData<Int>()
    val weight: MutableLiveData<Int>
        get() = _weight

    val canSave =
        MediatorLiveData<Boolean>().apply {
            fun update() {
                value = _gender.value != null && _weight.value != null
            }

            addSource(_gender) { update() }
            addSource(_weight) { update() }
        }

    fun updateWeight(value: Int) {
        _weight.value = value
    }

    fun updateGender(gender: Gender) {
        _gender.value = gender
    }
}
