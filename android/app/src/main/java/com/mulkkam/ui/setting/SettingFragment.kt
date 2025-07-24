package com.mulkkam.ui.setting

import android.os.Bundle
import android.view.View
import com.mulkkam.databinding.FragmentSettingBinding
import com.mulkkam.ui.binding.BindingFragment
import com.mulkkam.ui.main.Refreshable
import com.mulkkam.ui.setting.adapter.SettingAdapter
import com.mulkkam.ui.setting.model.SettingsMenu
import com.mulkkam.ui.settinggoal.SettingGoalActivity
import com.mulkkam.ui.settingprofile.SettingProfileActivity
import com.mulkkam.ui.settingwater.SettingWaterActivity

class SettingFragment :
    BindingFragment<FragmentSettingBinding>(FragmentSettingBinding::inflate),
    Refreshable {
    private val settingAdapter by lazy {
        SettingAdapter { settingMenu ->
            handleSettingClick(settingMenu)
        }
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvSettingOptions.adapter = settingAdapter
        settingAdapter.submitList(SettingsMenu.entries)
    }

    private fun handleSettingClick(menu: SettingsMenu) {
        when (menu) {
            SettingsMenu.SETTING_PROFILE -> {
                val intent = SettingProfileActivity.newIntent(requireContext())
                startActivity(intent)
            }

            SettingsMenu.SETTING_WATER -> {
                val intent = SettingWaterActivity.newIntent(requireContext())
                startActivity(intent)
            }

            SettingsMenu.SETTING_GOAL -> {
                val intent = SettingGoalActivity.newIntent(requireContext())
                startActivity(intent)
            }
        }
    }

    override fun onSelected() {
        // TODO: 화면 전환 시 필요한 작업을 구현합니다.
    }
}
