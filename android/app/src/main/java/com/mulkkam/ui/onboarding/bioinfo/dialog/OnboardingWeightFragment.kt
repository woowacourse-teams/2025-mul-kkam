package com.mulkkam.ui.onboarding.bioinfo.dialog

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.mulkkam.databinding.FragmentWeightBinding
import com.mulkkam.ui.binding.BindingBottomSheetDialogFragment
import com.mulkkam.ui.onboarding.bioinfo.BioInfoViewModel
import com.mulkkam.ui.util.setSingleClickListener

class OnboardingWeightFragment :
    BindingBottomSheetDialogFragment<FragmentWeightBinding>(
        FragmentWeightBinding::inflate,
    ) {
    private val parentViewModel: BioInfoViewModel by activityViewModels()

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initWeight()
        initClickListeners()
    }

    private fun initWeight() {
        binding.npWeightChoose.apply {
            minValue = WEIGHT_MIN
            maxValue = WEIGHT_MAX
            value = parentViewModel.weight.value ?: WEIGHT_DEFAULT
        }
    }

    private fun initClickListeners() {
        binding.ivWeightClose.setSingleClickListener { dismiss() }

        binding.tvComplete.setSingleClickListener {
            parentViewModel.updateWeight(binding.npWeightChoose.value)
            dismiss()
        }
    }

    companion object {
        private const val WEIGHT_MAX: Int = 150
        private const val WEIGHT_MIN: Int = 25
        private const val WEIGHT_DEFAULT: Int = 50
    }
}
