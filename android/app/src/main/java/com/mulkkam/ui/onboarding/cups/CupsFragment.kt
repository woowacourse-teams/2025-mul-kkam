package com.mulkkam.ui.onboarding.cups

import android.os.Bundle
import android.view.View
import com.mulkkam.databinding.FragmentCupsBinding
import com.mulkkam.ui.util.binding.BindingFragment

class CupsFragment :
    BindingFragment<FragmentCupsBinding>(
        FragmentCupsBinding::inflate,
    ) {
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
    }
}
