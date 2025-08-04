package com.mulkkam.ui.onboarding.terms.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mulkkam.R
import com.mulkkam.databinding.ItemTermsAgreementBinding
import com.mulkkam.ui.onboarding.terms.TermsAgreementUiModel

class TermsAgreementViewHolder(
    private val binding: ItemTermsAgreementBinding,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(termsAgreement: TermsAgreementUiModel) {
        val requirementLabel =
            if (termsAgreement.isRequired) R.string.terms_required_suffix else R.string.terms_optional_suffix
        binding.tvLabel.text =
            binding.root.context.getString(termsAgreement.labelId, requirementLabel)
        binding.cbAgreement.isChecked = termsAgreement.isChecked
    }

    companion object {
        fun from(parent: ViewGroup): TermsAgreementViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemTermsAgreementBinding.inflate(inflater, parent, false)
            return TermsAgreementViewHolder(binding)
        }
    }
}
