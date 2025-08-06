package com.mulkkam.ui.onboarding.terms.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mulkkam.ui.onboarding.terms.TermsAgreementUiModel

class TermsAdapter(
    private val termsAgreementHandler: TermsAgreementHandler,
) : RecyclerView.Adapter<TermsAgreementViewHolder>() {
    private val termsAgreements = mutableListOf<TermsAgreementUiModel>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): TermsAgreementViewHolder = TermsAgreementViewHolder.from(parent, termsAgreementHandler)

    override fun onBindViewHolder(
        holder: TermsAgreementViewHolder,
        position: Int,
    ) {
        val terms = termsAgreements[position]
        holder.bind(terms)
    }

    override fun getItemCount(): Int = termsAgreements.size

    fun submitList(terms: List<TermsAgreementUiModel>) {
        termsAgreements.clear()
        termsAgreements.addAll(terms)

        if (termsAgreements.isEmpty() && terms.isNotEmpty()) {
            notifyItemRangeInserted(0, terms.size)
        } else {
            notifyItemRangeChanged(0, termsAgreements.size)
        }
    }
}
