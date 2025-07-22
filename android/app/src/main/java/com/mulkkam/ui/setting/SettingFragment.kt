package com.mulkkam.ui.setting

import com.mulkkam.databinding.FragmentSettingBinding
import com.mulkkam.ui.binding.BindingFragment
import com.mulkkam.ui.main.Refreshable

class SettingFragment :
    BindingFragment<FragmentSettingBinding>(
        FragmentSettingBinding::inflate,
    ),
    Refreshable {
    override fun onSelected() {
        // TODO: 화면 전환 시 필요한 작업을 구현합니다.
    }
}
