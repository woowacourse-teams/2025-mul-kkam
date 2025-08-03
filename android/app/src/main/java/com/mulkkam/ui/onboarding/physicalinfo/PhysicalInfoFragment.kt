package com.mulkkam.ui.onboarding.physicalinfo

import android.os.Bundle
import android.view.View
import com.mulkkam.databinding.FragmentNickNameBinding.inflate
import com.mulkkam.databinding.FragmentPhysicalInfoBinding
import com.mulkkam.ui.binding.BindingFragment

class PhysicalInfoFragment :
    BindingFragment<FragmentPhysicalInfoBinding>(
        FragmentPhysicalInfoBinding::inflate,
    ) {
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
    }
}
