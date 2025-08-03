package com.mulkkam.ui.onboarding.terms.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mulkkam.databinding.ItemTermsAgreementBinding
import com.mulkkam.ui.onboarding.terms.TermsAgreementUiModel

class TermsAgreementViewHolder(
    private val binding: ItemTermsAgreementBinding,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(termsAgreement: TermsAgreementUiModel) {
        val asdf = if (termsAgreement.isRequired) " (필수)" else " (선택)"
        binding.tvLabel.text = termsAgreement.title + asdf
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
