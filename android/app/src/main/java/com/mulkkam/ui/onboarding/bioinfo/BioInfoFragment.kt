package com.mulkkam.ui.onboarding.bioinfo

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.mulkkam.R
import com.mulkkam.databinding.FragmentBioInfoBinding
import com.mulkkam.ui.binding.BindingFragment
import com.mulkkam.ui.onboarding.OnboardingViewModel
import com.mulkkam.ui.onboarding.bioinfo.dialog.WeightFragment
import com.mulkkam.ui.util.getAppearanceSpannable

class BioInfoFragment :
    BindingFragment<FragmentBioInfoBinding>(
        FragmentBioInfoBinding::inflate,
    ) {
    private val weightFragment: WeightFragment by lazy {
        WeightFragment()
    }
    private val parentViewModel: OnboardingViewModel by activityViewModels()

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initTextAppearance()
        initClickListeners()
    }

    private fun initTextAppearance() {
        binding.tvViewLabel.text =
            getString(R.string.bio_info_input_hint).getAppearanceSpannable(
                requireContext(),
                R.style.title1,
                getString(R.string.bio_info_input_hint_highlight),
            )
    }

    private fun initClickListeners() {
        binding.tvNext.setOnClickListener {
            parentViewModel.moveToNextStep()
        }

        binding.tvWeight.setOnClickListener {
            weightFragment.show(parentFragmentManager, null)
        }
    }
}
