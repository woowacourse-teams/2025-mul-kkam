package com.mulkkam.ui.onboarding.terms

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.mulkkam.R
import com.mulkkam.databinding.FragmentTermsBinding
import com.mulkkam.ui.binding.BindingFragment
import com.mulkkam.ui.onboarding.OnboardingViewModel
import com.mulkkam.ui.onboarding.terms.adapter.TermsAdapter
import com.mulkkam.ui.util.getAppearanceSpannable

class TermsFragment :
    BindingFragment<FragmentTermsBinding>(
        FragmentTermsBinding::inflate,
    ) {
    private val termsAdapter: TermsAdapter by lazy {
        TermsAdapter()
    }

    private val parentViewModel: OnboardingViewModel by activityViewModels()

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        initTextAppearance()
        initTermsAdapter()
        initTermsAgreements()
        initClickListeners()
    }

    private fun initTextAppearance() {
        binding.tvTermsLabel.text =
            getString(R.string.terms_agree_hint).getAppearanceSpannable(
                requireContext(),
                R.style.title1,
                getString(R.string.terms_agree_hint_highlight),
            )
    }

    private fun initTermsAdapter() {
        binding.rvList.adapter = termsAdapter
    }

    private fun initTermsAgreements() {
        val terms =
            listOf(
                TermsAgreementUiModel(R.string.terms_agree_service, true),
                TermsAgreementUiModel(R.string.terms_agree_privacy, true),
                TermsAgreementUiModel(R.string.terms_agree_night_notification, false),
                TermsAgreementUiModel(R.string.terms_agree_marketing, false),
            )
        termsAdapter.submitList(terms)
    }

    private fun initClickListeners() {
        binding.tvNext.setOnClickListener {
            parentViewModel.moveToNextStep()
        }
    }
}
