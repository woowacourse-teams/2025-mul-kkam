package com.mulkkam.ui.onboarding.terms

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.mulkkam.R
import com.mulkkam.databinding.FragmentTermsBinding
import com.mulkkam.ui.binding.BindingFragment
import com.mulkkam.ui.onboarding.OnboardingViewModel
import com.mulkkam.ui.onboarding.terms.adapter.TermsAdapter
import com.mulkkam.ui.util.extensions.applyImeMargin
import com.mulkkam.ui.util.extensions.getAppearanceSpannable
import com.mulkkam.ui.util.extensions.setSingleClickListener
import kotlin.collections.find

class TermsFragment :
    BindingFragment<FragmentTermsBinding>(
        FragmentTermsBinding::inflate,
    ) {
    private val termsAdapter: TermsAdapter by lazy {
        TermsAdapter {
            viewModel.updateCheckState(it)
        }
    }

    private val parentViewModel: OnboardingViewModel by activityViewModels()
    private val viewModel: TermsViewModel by viewModels()

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        initTextAppearance()
        initTermsAdapter()
        initClickListeners()
        initObservers()
        binding.tvNext.applyImeMargin()
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

    private fun initClickListeners() {
        with(binding) {
            tvNext.setSingleClickListener {
                parentViewModel.moveToNextStep()
            }

            llAllCheck.setOnClickListener {
                viewModel.checkAllAgreement()
            }
        }
    }

    private fun initObservers() {
        viewModel.termsAgreements.observe(viewLifecycleOwner) {
            termsAdapter.submitList(it)

            parentViewModel.updateTermsAgreementState(
                it.find { it.labelId == R.string.terms_agree_marketing }?.isChecked == true,
                it.find { it.labelId == R.string.terms_agree_night_notification }?.isChecked == true,
            )
        }

        viewModel.isAllChecked.observe(viewLifecycleOwner) {
            binding.cbAllCheck.isChecked = it
        }

        viewModel.canNext.observe(viewLifecycleOwner) {
            updateNextButtonEnabled(it)
        }
    }

    private fun updateNextButtonEnabled(enabled: Boolean) {
        binding.tvNext.isEnabled = enabled
        if (enabled) {
            binding.tvNext.backgroundTintList =
                ColorStateList.valueOf(getColor(requireContext(), R.color.primary_200))
        } else {
            binding.tvNext.backgroundTintList =
                ColorStateList.valueOf(getColor(requireContext(), R.color.gray_200))
        }
    }
}
