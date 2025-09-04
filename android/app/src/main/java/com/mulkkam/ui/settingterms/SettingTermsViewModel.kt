package com.mulkkam.ui.settingterms

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mulkkam.R

class SettingTermsViewModel : ViewModel() {
    private val _terms: MutableLiveData<List<TermsUiModel>> = MutableLiveData()
    val terms: LiveData<List<TermsUiModel>> get() = _terms

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
