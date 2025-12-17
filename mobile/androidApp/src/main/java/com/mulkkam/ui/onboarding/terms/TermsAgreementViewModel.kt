package com.mulkkam.ui.onboarding.terms

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class TermsAgreementViewModel : ViewModel() {
    private val _termsAgreements: MutableStateFlow<List<TermsAgreementUiModel>> = MutableStateFlow(emptyList())
    val termsAgreements: StateFlow<List<TermsAgreementUiModel>> get() = _termsAgreements

    val isAllChecked: StateFlow<Boolean> =
        termsAgreements
            .map { list -> list.all { it.isChecked } }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = false,
            )

    val canNext: StateFlow<Boolean> =
        termsAgreements
            .map { list -> list.filter { it.isRequired }.all { it.isChecked } }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = false,
            )

    init {
        _termsAgreements.value = TERMS_AGREEMENTS
    }

    fun toggleCheckState(termsAgreement: TermsAgreementUiModel) {
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

    companion object {
        private val TERMS_AGREEMENTS: List<TermsAgreementUiModel> =
            listOf(
                TermsAgreementUiModel(
                    labelId = R.string.terms_agree_service,
                    isRequired = true,
                    uri = R.string.terms_service,
                ),
                TermsAgreementUiModel(
                    labelId = R.string.terms_agree_privacy,
                    isRequired = true,
                    uri = R.string.terms_privacy,
                ),
                TermsAgreementUiModel(
                    labelId = R.string.terms_agree_night_notification,
                    isRequired = false,
                    uri = R.string.terms_night_notification,
                ),
                TermsAgreementUiModel(
                    labelId = R.string.terms_agree_marketing,
                    isRequired = false,
                    uri = R.string.terms_marketing,
                ),
            )
    }
}
