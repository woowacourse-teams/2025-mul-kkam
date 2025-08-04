package com.mulkkam.ui.onboarding.bioinfo.dialog

import android.os.Bundle
import android.view.View
import com.mulkkam.databinding.FragmentWeightBinding
import com.mulkkam.ui.binding.BindingBottomSheetDialogFragment

class WeightFragment :
    BindingBottomSheetDialogFragment<FragmentWeightBinding>(
        FragmentWeightBinding::inflate,
    ) {
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initWeight()
        initClickListeners()
    }

    private fun initWeight() {
        binding.npWeightChoose.maxValue = WEIGHT_MAX_VALUE
        binding.npWeightChoose.minValue = WEIGHT_MIN_VALUE
        binding.npWeightChoose.value = WEIGHT_DEFAULT_VALUE
    }

    private fun initClickListeners() {
        binding.ivWeightClose.setOnClickListener { dismiss() }

        binding.tvComplete.setOnClickListener {
            // TODO: 몸무게 저장 로직 필요 ( 부모 뷰모델을 통해 저장할 듯 )
            dismiss()
        }
    }

    companion object {
        private const val WEIGHT_MAX_VALUE: Int = 150
        private const val WEIGHT_MIN_VALUE: Int = 25
        private const val WEIGHT_DEFAULT_VALUE: Int = 50
    }
}
