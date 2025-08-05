package com.mulkkam.ui.onboarding.targetamount

import android.os.Bundle
import android.view.View
import com.mulkkam.R
import com.mulkkam.databinding.FragmentTargetAmountBinding
import com.mulkkam.ui.binding.BindingFragment
import com.mulkkam.ui.onboarding.dialog.CompleteDialogFragment
import com.mulkkam.ui.util.getAppearanceSpannable

class TargetAmountFragment :
    BindingFragment<FragmentTargetAmountBinding>(
        FragmentTargetAmountBinding::inflate,
    ) {
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
            getString(R.string.target_amount_input_hint).getAppearanceSpannable(
                requireContext(),
                R.style.title1,
                getString(R.string.target_amount_input_hint_highlight),
            )
    }

    private fun initClickListeners() {
        binding.tvComplete.setOnClickListener {
            val dialog = CompleteDialogFragment()
            dialog.isCancelable = false
            dialog.show(parentFragmentManager, null)
        }
    }
}
