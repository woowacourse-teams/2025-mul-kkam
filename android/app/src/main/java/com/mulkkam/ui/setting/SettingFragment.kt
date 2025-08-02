package com.mulkkam.ui.setting

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.mulkkam.R
import com.mulkkam.databinding.FragmentSettingBinding
import com.mulkkam.ui.binding.BindingFragment
import com.mulkkam.ui.main.Refreshable
import com.mulkkam.ui.setting.adapter.SettingAdapter
import com.mulkkam.ui.setting.adapter.SettingItem
import com.mulkkam.ui.setting.model.SettingType
import com.mulkkam.ui.settinggoal.SettingGoalActivity
import com.mulkkam.ui.settingprofile.SettingProfileActivity
import com.mulkkam.ui.settingwater.SettingWaterActivity

class SettingFragment :
    BindingFragment<FragmentSettingBinding>(FragmentSettingBinding::inflate),
    Refreshable {
    private val settingAdapter by lazy { handleSettingClick() }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvSettingOptions.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSettingOptions.adapter = settingAdapter

        val settingItems = createSettingItems()
        settingAdapter.submitList(settingItems)
    }

    private fun handleSettingClick() =
        SettingAdapter(
            object : SettingAdapter.Handler {
                override fun onNormalItemClick(item: SettingItem.NormalItem) {
                    handleNormalClick(item.type)
                }

                override fun onSwitchChanged(
                    item: SettingItem.SwitchItem,
                    isChecked: Boolean,
                ) {
                    handleSwitchChange(item.type, isChecked)
                }
            },
        )

    private fun createSettingItems(): List<SettingItem> =
        listOf(
            SettingItem.TitleItem(getString(R.string.setting_section_account)),
            SettingItem.NormalItem(getString(R.string.setting_profile_edit_nickname_label), SettingType.Normal.Nickname),
            SettingItem.NormalItem(getString(R.string.setting_item_body_info), SettingType.Normal.BodyInfo),
            SettingItem.DividerItem,
            SettingItem.TitleItem(getString(R.string.setting_section_water)),
            SettingItem.NormalItem(getString(R.string.setting_water_toolbar_title), SettingType.Normal.MyCup),
            SettingItem.NormalItem(getString(R.string.setting_goal_toolbar_title), SettingType.Normal.Goal),
            SettingItem.DividerItem,
            SettingItem.TitleItem(getString(R.string.setting_section_notification)),
            SettingItem.NormalItem(getString(R.string.setting_item_notification), SettingType.Normal.Notification),
            SettingItem.SwitchItem(getString(R.string.setting_item_marketing), false, SettingType.Switch.Marketing),
            SettingItem.SwitchItem(getString(R.string.setting_item_night), false, SettingType.Switch.Night),
            SettingItem.DividerItem,
            SettingItem.TitleItem(getString(R.string.setting_section_permission)),
            SettingItem.SwitchItem(getString(R.string.setting_item_health_connect), false, SettingType.Switch.HealthConnect),
        )

    private fun handleNormalClick(type: SettingType.Normal) {
        when (type) {
            SettingType.Normal.Nickname -> startActivity(SettingProfileActivity.newIntent(requireContext()))
            SettingType.Normal.BodyInfo -> {
                // TODO: 신체 정보 설정 화면 이동
            }

            SettingType.Normal.MyCup -> startActivity(SettingWaterActivity.newIntent(requireContext()))
            SettingType.Normal.Goal -> startActivity(SettingGoalActivity.newIntent(requireContext()))
            SettingType.Normal.Notification -> {
                // TODO: 푸시 알림 설정 화면 이동
            }
        }
    }

    private fun handleSwitchChange(
        type: SettingType.Switch,
        isChecked: Boolean,
    ) {
        when (type) {
            SettingType.Switch.HealthConnect -> {
                // TODO: 헬스 커넥트 연동 상태 저장
            }

            SettingType.Switch.Marketing -> {
                // TODO: 마케팅 수신 허용 상태 저장
            }

            SettingType.Switch.Night -> {
                // TODO: 야간 알림 허용 상태 저장
            }
        }
    }
}
