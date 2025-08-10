package com.mulkkam.ui.settingbioinfo.dialog

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.mulkkam.databinding.FragmentWeightBinding
import com.mulkkam.domain.model.BioWeight.Companion.WEIGHT_DEFAULT
import com.mulkkam.domain.model.BioWeight.Companion.WEIGHT_MAX
import com.mulkkam.domain.model.BioWeight.Companion.WEIGHT_MIN
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
            minValue = WEIGHT_MIN
            maxValue = WEIGHT_MAX
            value = parentViewModel.weight.value?.value ?: WEIGHT_DEFAULT
        }
    }

    private fun initClickListeners() {
        binding.ivWeightClose.setOnClickListener { dismiss() }

        binding.tvComplete.setOnClickListener {
            parentViewModel.updateWeight(binding.npWeightChoose.value)
            dismiss()
        }
    }
}
