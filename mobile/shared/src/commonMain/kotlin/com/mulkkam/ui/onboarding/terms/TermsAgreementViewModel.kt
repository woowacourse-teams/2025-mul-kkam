package com.mulkkam.ui.onboarding.terms

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.ui.onboarding.terms.model.TermsAgreementUiModel
import com.mulkkam.ui.onboarding.terms.model.TermsType
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

    fun initAgreements(
        isServiceAgreed: Boolean = false,
        isPrivacyPolicyAgreed: Boolean = false,
        isMarketingNotificationAgreed: Boolean,
        isNightNotificationAgreed: Boolean,
    ) {
        _termsAgreements.value =
            _termsAgreements.value.map { agreement ->
                when (agreement.type) {
                    TermsType.MARKETING -> agreement.copy(isChecked = isMarketingNotificationAgreed)
                    TermsType.NIGHT_NOTIFICATION -> agreement.copy(isChecked = isNightNotificationAgreed)
                    TermsType.SERVICE -> agreement.copy(isChecked = isServiceAgreed)
                    TermsType.PRIVACY -> agreement.copy(isChecked = isPrivacyPolicyAgreed)
                }
            }
    }

    fun toggleCheckState(termsAgreement: TermsAgreementUiModel) {
        _termsAgreements.value =
            _termsAgreements.value.orEmpty().map { agreement ->
                if (agreement.type == termsAgreement.type) {
                    agreement.copy(isChecked = !agreement.isChecked)
                } else {
                    agreement
                }
            }
    }

    fun checkAllAgreement() {
        val hasUncheckedAgreement: Boolean = termsAgreements.value.orEmpty().any { !it.isChecked }
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
                    type = TermsType.SERVICE,
                    isRequired = true,
                ),
                TermsAgreementUiModel(
                    type = TermsType.PRIVACY,
                    isRequired = true,
                ),
                TermsAgreementUiModel(
                    type = TermsType.NIGHT_NOTIFICATION,
                    isRequired = false,
                ),
                TermsAgreementUiModel(
                    type = TermsType.MARKETING,
                    isRequired = false,
                ),
            )
    }
}
