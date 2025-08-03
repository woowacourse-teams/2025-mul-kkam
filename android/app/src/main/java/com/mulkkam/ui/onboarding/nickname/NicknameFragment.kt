package com.mulkkam.ui.onboarding.nickname

import android.os.Bundle
import android.view.View
import com.mulkkam.databinding.FragmentNickNameBinding
import com.mulkkam.ui.binding.BindingFragment

class NicknameFragment :
    BindingFragment<FragmentNickNameBinding>(
        FragmentNickNameBinding::inflate,
    ) {
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
    }
}
