package com.mulkkam.ui.settingterms.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mulkkam.databinding.ItemSettingNormalBinding
import com.mulkkam.ui.settingterms.TermsUiModel
import com.mulkkam.ui.util.extensions.setSingleClickListener

class TermsViewHolder(
    termsAgreementHandler: TermsHandler,
    private val binding: ItemSettingNormalBinding,
) : RecyclerView.ViewHolder(binding.root) {
    private var termsAgreement: TermsUiModel? = null

    init {
        initClickListener(termsAgreementHandler)
    }

    fun bind(termsAgreement: TermsUiModel) {
        this.termsAgreement = termsAgreement

        with(binding) {
            tvLabel.text =
                root.context.getString(termsAgreement.labelId)
        }
    }

    private fun initClickListener(handler: TermsHandler) {
        binding.root.setSingleClickListener {
            termsAgreement?.let {
                handler.loadToTermsPage(it)
            }
        }
    }

    interface TermsHandler {
        fun loadToTermsPage(termsAgreement: TermsUiModel)
    }

    companion object {
        fun from(
            parent: ViewGroup,
            termsAgreementHandler: TermsHandler,
        ): TermsViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemSettingNormalBinding.inflate(inflater, parent, false)
            return TermsViewHolder(termsAgreementHandler, binding)
        }
    }
}
