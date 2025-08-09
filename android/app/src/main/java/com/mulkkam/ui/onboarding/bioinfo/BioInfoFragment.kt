package com.mulkkam.ui.onboarding.bioinfo

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.activityViewModels
import com.mulkkam.R
import com.mulkkam.databinding.FragmentBioInfoBinding
import com.mulkkam.domain.model.Gender
import com.mulkkam.domain.model.Gender.FEMALE
import com.mulkkam.domain.model.Gender.MALE
import com.mulkkam.ui.binding.BindingFragment
import com.mulkkam.ui.onboarding.OnboardingViewModel
import com.mulkkam.ui.onboarding.bioinfo.dialog.OnboardingWeightFragment
import com.mulkkam.ui.util.getAppearanceSpannable
import com.mulkkam.ui.util.setSingleClickListener

class BioInfoFragment :
    BindingFragment<FragmentBioInfoBinding>(
        FragmentBioInfoBinding::inflate,
    ) {
    private val weightFragment: OnboardingWeightFragment by lazy {
        OnboardingWeightFragment()
    }
    private val parentViewModel: OnboardingViewModel by activityViewModels()
    private val viewModel: BioInfoViewModel by activityViewModels()

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initTextAppearance()
        initClickListeners()
        initObservers()
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
        with(binding) {
            tvNext.setSingleClickListener {
                parentViewModel.updateBioInfo(viewModel.gender.value, viewModel.weight.value)
                parentViewModel.moveToNextStep()
            }

            tvWeight.setOnClickListener {
                weightFragment.show(parentFragmentManager, null)
            }

            tvGenderMale.setOnClickListener {
                viewModel.updateGender(MALE)
            }

            tvGenderFemale.setOnClickListener {
                viewModel.updateGender(FEMALE)
            }
        }
    }

    private fun initObservers() {
        viewModel.weight.observe(viewLifecycleOwner) { weight ->
            binding.tvWeight.text = getString(R.string.bio_info_weight_format, weight)
        }

        viewModel.gender.observe(viewLifecycleOwner) { selectedGender ->
            changeGender(selectedGender)
        }

        viewModel.canNext.observe(viewLifecycleOwner) { enabled ->
            updateNextButtonEnabled(enabled)
        }
    }

    private fun changeGender(selectedGender: Gender) {
        if (selectedGender == MALE) {
            selectGender(binding.tvGenderMale)
            deselectGender(binding.tvGenderFemale)
        } else {
            selectGender(binding.tvGenderFemale)
            deselectGender(binding.tvGenderMale)
        }
    }

    private fun selectGender(gender: TextView) {
        with(gender) {
            isSelected = true
            setTextColor(getColor(requireContext(), R.color.white))
            backgroundTintList =
                ColorStateList.valueOf(getColor(requireContext(), R.color.primary_100))
        }
    }

    private fun deselectGender(gender: TextView) {
        with(gender) {
            isSelected = false
            setTextColor(getColor(requireContext(), R.color.gray_400))
            backgroundTintList =
                ColorStateList.valueOf(
                    getColor(
                        requireContext(),
                        R.color.gray_200,
                    ),
                )
        }
    }

    private fun updateNextButtonEnabled(enabled: Boolean) {
        binding.tvNext.isEnabled = enabled
        if (enabled) {
            binding.tvNext.backgroundTintList =
                ColorStateList.valueOf(getColor(requireContext(), R.color.primary_200))
        }
    }
}
