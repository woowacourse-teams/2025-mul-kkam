package com.mulkkam.ui.settingterms

import androidx.lifecycle.ViewModel
import com.mulkkam.ui.settingterms.model.SettingTermsType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingTermsViewModel : ViewModel() {
    private val _terms: MutableStateFlow<List<SettingTermsType>> = MutableStateFlow(SettingTermsType.entries)
    val terms: StateFlow<List<SettingTermsType>> get() = _terms.asStateFlow()
}
