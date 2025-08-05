package com.mulkkam.ui.onboarding.terms

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.mulkkam.R

class TermsViewModel : ViewModel() {
    private val _termsAgreements = MutableLiveData<List<TermsAgreementUiModel>>()
    val termsAgreements: LiveData<List<TermsAgreementUiModel>> get() = _termsAgreements

    val isAllChecked: LiveData<Boolean> =
        termsAgreements.map {
            it.all { agreement -> agreement.isChecked }
        }

    val canNext: LiveData<Boolean> =
        termsAgreements.map {
            it
                .filter { it.isRequired }
                .all { it.isChecked }
        }

    init {
        _termsAgreements.value =
            listOf(
                TermsAgreementUiModel(R.string.terms_agree_service, true),
                TermsAgreementUiModel(R.string.terms_agree_privacy, true),
                TermsAgreementUiModel(R.string.terms_agree_night_notification, false),
                TermsAgreementUiModel(R.string.terms_agree_marketing, false),
            )
    }

    fun updateCheckState(termsAgreement: TermsAgreementUiModel) {
        _termsAgreements.value =
            _termsAgreements.value.orEmpty().map { agreement ->
                if (agreement == termsAgreement) {
                    agreement.copy(isChecked = !agreement.isChecked)
                } else {
                    agreement
                }
            }
    }

    fun checkAllAgreement() {
        val hasUncheckedAgreement = termsAgreements.value.orEmpty().any { !it.isChecked }
        if (hasUncheckedAgreement) {
            _termsAgreements.value =
                _termsAgreements.value.orEmpty().map { agreement ->
                    agreement.copy(isChecked = true)
                }
        } else {
            _termsAgreements.value =
                _termsAgreements.value.orEmpty().map { agreement ->
                    agreement.copy(isChecked = false)
                }
        }
    }
}
