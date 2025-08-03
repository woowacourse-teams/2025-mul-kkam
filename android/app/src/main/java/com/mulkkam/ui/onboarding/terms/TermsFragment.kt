package com.mulkkam.ui.onboarding.terms

import android.os.Bundle
import android.view.View
import com.mulkkam.databinding.FragmentHistoryBinding.inflate
import com.mulkkam.databinding.FragmentTermsBinding
import com.mulkkam.ui.binding.BindingFragment

class TermsFragment :
    BindingFragment<FragmentTermsBinding>(
        FragmentTermsBinding::inflate,
    ) {
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
    }
}
