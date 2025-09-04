package com.mulkkam.ui.onboarding.terms.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getString
import androidx.recyclerview.widget.RecyclerView
import com.mulkkam.R
import com.mulkkam.databinding.ItemTermsAgreementBinding
import com.mulkkam.ui.onboarding.terms.TermsAgreementUiModel

class TermsAgreementViewHolder(
    termsAgreementHandler: TermsAgreementHandler,
    private val binding: ItemTermsAgreementBinding,
) : RecyclerView.ViewHolder(binding.root) {
    private var termsAgreement: TermsAgreementUiModel? = null

    init {
        initClickListener(termsAgreementHandler)
    }

    fun bind(termsAgreement: TermsAgreementUiModel) {
        this.termsAgreement = termsAgreement

        with(binding) {
            val requirementLabel =
                if (termsAgreement.isRequired) {
                    root.context.getString(R.string.terms_required_suffix)
                } else {
                    root.context.getString(R.string.terms_optional_suffix)
                }
            tvLabel.text =
                root.context.getString(termsAgreement.labelId, requirementLabel)

            cbAgreement.isChecked = termsAgreement.isChecked
        }
    }

    private fun initClickListener(handler: TermsAgreementHandler) {
        binding.llAgreement.setOnClickListener {
            termsAgreement?.let {
                handler.checkAgreement(it)
            }
        }

        binding.ivDescriptionPage.setOnClickListener {
            termsAgreement?.let {
                handler.loadToTermsPage(it)
            }
        }
    }

    interface TermsAgreementHandler {
        fun checkAgreement(termsAgreement: TermsAgreementUiModel)

        fun loadToTermsPage(termsAgreement: TermsAgreementUiModel)
    }

    companion object {
        fun from(
            parent: ViewGroup,
            termsAgreementHandler: TermsAgreementHandler,
        ): TermsAgreementViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemTermsAgreementBinding.inflate(inflater, parent, false)
            return TermsAgreementViewHolder(termsAgreementHandler, binding)
        }
    }
}
