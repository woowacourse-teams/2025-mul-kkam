package com.mulkkam.ui.settingterms.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mulkkam.ui.settingterms.TermsUiModel
import com.mulkkam.ui.settingterms.adapter.TermsViewHolder.TermsHandler

class TermsAdapter(
    private val termsHandler: TermsHandler,
) : RecyclerView.Adapter<TermsViewHolder>() {
    private val termsItems = mutableListOf<TermsUiModel>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): TermsViewHolder = TermsViewHolder.from(parent, termsHandler)

    override fun onBindViewHolder(
        holder: TermsViewHolder,
        position: Int,
    ) {
        holder.bind(termsItems[position])
    }

    override fun getItemCount(): Int = termsItems.size

    fun submitList(terms: List<TermsUiModel>) {
        termsItems.clear()
        termsItems.addAll(terms)

        if (termsItems.isEmpty() && terms.isNotEmpty()) {
            notifyItemRangeInserted(0, terms.size)
        } else {
            notifyItemRangeChanged(0, termsItems.size)
        }
    }
}
