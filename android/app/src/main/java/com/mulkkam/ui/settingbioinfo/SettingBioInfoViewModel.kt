package com.mulkkam.ui.settingbioinfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.di.RepositoryInjection
import com.mulkkam.domain.Gender
import com.mulkkam.ui.util.MutableSingleLiveData
import com.mulkkam.ui.util.SingleLiveData
import kotlinx.coroutines.launch

class SettingBioInfoViewModel : ViewModel() {
    private val _gender = MutableLiveData<Gender?>()
    val gender: LiveData<Gender?>
        get() = _gender

    private val _weight = MutableLiveData<Int?>()
    val weight: MutableLiveData<Int?>
        get() = _weight

    private val _onBioInfoChanged = MutableSingleLiveData<Unit>()
    val onBioInfoChanged: SingleLiveData<Unit>
        get() = _onBioInfoChanged

    val canSave =
        MediatorLiveData<Boolean>().apply {
            fun update() {
                value = _gender.value != null && _weight.value != null
            }

            addSource(_gender) { update() }
            addSource(_weight) { update() }
        }

    init {
        loadMemberInfo()
    }

    private fun loadMemberInfo() {
        viewModelScope.launch {
            val result = RepositoryInjection.membersRepository.getMembers()
            runCatching {
                val memberInfo = result.getOrError()
                _gender.value = memberInfo.gender
                _weight.value = memberInfo.weight
            }.onFailure {
                // TODO: 예외 처리
            }
        }
    }

    fun updateWeight(value: Int) {
        _weight.value = value
    }

    fun updateGender(gender: Gender) {
        _gender.value = gender
    }

    fun saveBioInfo() {
        viewModelScope.launch {
            val result =
                RepositoryInjection.membersRepository.postMembersPhysicalAttributes(
                    gender = _gender.value ?: return@launch,
                    weight = _weight.value ?: return@launch,
                )
            runCatching {
                result.getOrError()
                _onBioInfoChanged.setValue(Unit)
            }.onFailure {
                // TODO: 예외 처리
            }
        }
    }
}
