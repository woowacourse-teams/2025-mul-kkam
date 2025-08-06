package com.mulkkam.ui.settingbioinfo.dialog

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.mulkkam.databinding.FragmentWeightBinding
import com.mulkkam.ui.binding.BindingBottomSheetDialogFragment
import com.mulkkam.ui.settingbioinfo.SettingBioInfoViewModel

class SettingWeightFragment :
    BindingBottomSheetDialogFragment<FragmentWeightBinding>(
        FragmentWeightBinding::inflate,
    ) {
    private val parentViewModel: SettingBioInfoViewModel by activityViewModels()

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
            minValue = WEIGHT_MIN_VALUE
            maxValue = WEIGHT_MAX_VALUE
            value = WEIGHT_DEFAULT_VALUE
        }
    }

    private fun initClickListeners() {
        binding.ivWeightClose.setOnClickListener { dismiss() }

        binding.tvComplete.setOnClickListener {
            parentViewModel.updateWeight(binding.npWeightChoose.value)
            dismiss()
        }
    }

    companion object {
        private const val WEIGHT_MAX_VALUE: Int = 150
        private const val WEIGHT_MIN_VALUE: Int = 25
        private const val WEIGHT_DEFAULT_VALUE: Int = 50
    }
}
