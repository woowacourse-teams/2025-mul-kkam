package com.mulkkam.ui.onboarding.terms

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.mulkkam.R
import com.mulkkam.ui.util.MutableSingleLiveData
import com.mulkkam.ui.util.SingleLiveData

class TermsViewModel : ViewModel() {
    private val _termsAgreements = MutableLiveData<List<TermsAgreementUiModel>>()
    val termsAgreements: LiveData<List<TermsAgreementUiModel>> get() = _termsAgreements

    private val _tryHealthPermission: MutableSingleLiveData<Unit> = MutableSingleLiveData()
    val tryHealthPermission: SingleLiveData<Unit> get() = _tryHealthPermission

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

    fun updateHealthPermissionStatus(isGranted: Boolean) {
        _termsAgreements.value =
            _termsAgreements.value.orEmpty().map { agreement ->
                if (agreement.labelId == R.string.terms_agree_health_connect) {
                    agreement.copy(isChecked = isGranted)
                } else {
                    agreement
                }
            }
    }

    fun checkAllAgreement() {
        val hasUncheckedAgreement = termsAgreements.value.orEmpty().any { !it.isChecked }
        if (hasUncheckedAgreement) {
            requestHealthPermission()
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

    fun requestHealthPermission() {
        _tryHealthPermission.setValue(Unit)
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
                    labelId = R.string.terms_agree_health_connect,
                    isRequired = false,
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
