package com.mulkkam.ui.setting

import com.mulkkam.databinding.FragmentSettingBinding
import com.mulkkam.ui.binding.BindingFragment
import com.mulkkam.ui.main.MainActivity

class SettingFragment :
    BindingFragment<FragmentSettingBinding>(
        FragmentSettingBinding::inflate,
    ),
    MainActivity.Refreshable {
    override fun onSelected() {
    }
}
