package com.mulkkam.ui.onboarding.terms

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.TextAppearanceSpan
import android.view.View
import androidx.annotation.StyleRes
import androidx.fragment.app.activityViewModels
import com.mulkkam.R
import com.mulkkam.databinding.FragmentTermsBinding
import com.mulkkam.ui.binding.BindingFragment
import com.mulkkam.ui.onboarding.OnboardingViewModel
import com.mulkkam.ui.onboarding.terms.adapter.TermsAdapter

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

        initAppearance()
        initTermsAdapter()
        initTermsAgreements()
        initClickListeners()
    }

    private fun initTermsAdapter() {
        binding.rcvList.adapter = termsAdapter
    }

    private fun initTermsAgreements() {
        val terms =
            listOf(
                TermsAgreementUiModel(getString(R.string.terms_agree_service), true),
                TermsAgreementUiModel(getString(R.string.terms_agree_privacy), true),
                TermsAgreementUiModel(getString(R.string.terms_agree_night_notification), false),
                TermsAgreementUiModel(getString(R.string.terms_agree_marketing), false),
            )
        termsAdapter.submitList(terms)
    }

    private fun initAppearance() {
        binding.tvTermsLabel.text =
            getAppearanceSpannable(
                R.style.title1,
                getString(R.string.terms_agree_label),
                getString(R.string.terms_agree_terms_label),
            )
    }

    private fun getAppearanceSpannable(
        @StyleRes typographyResId: Int,
        fullText: String,
        vararg highlightedText: String,
    ): SpannableString {
        val spannable = SpannableString(fullText)

        highlightedText.forEach { target ->
            var startIndex = fullText.indexOf(target)
            spannable.setSpan(
                TextAppearanceSpan(context, typographyResId),
                startIndex,
                startIndex + target.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE,
            )
        }

        return spannable
    }

    private fun initClickListeners() {
        binding.tvNext.setOnClickListener {
            parentViewModel.moveToNextStep()
        }
    }
}
