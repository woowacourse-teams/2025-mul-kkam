package com.mulkkam.ui.settingterms

import androidx.lifecycle.ViewModel
import com.mulkkam.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingTermsViewModel : ViewModel() {
    private val _terms: MutableStateFlow<List<TermsUiModel>> = MutableStateFlow(emptyList())
    val terms: StateFlow<List<TermsUiModel>> get() = _terms.asStateFlow()

    init {
        _terms.value = TERMS_AGREEMENTS
    }

    companion object {
        private val TERMS_AGREEMENTS: List<TermsUiModel> =
            listOf(
                TermsUiModel(
                    labelId = R.string.setting_terms_agree_service,
                    uri = R.string.terms_service,
                ),
                TermsUiModel(
                    labelId = R.string.setting_terms_agree_privacy,
                    uri = R.string.terms_privacy,
                ),
                TermsUiModel(
                    labelId = R.string.setting_terms_agree_health_connect,
                ),
            )
    }
}
