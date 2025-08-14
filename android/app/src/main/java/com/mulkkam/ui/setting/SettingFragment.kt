package com.mulkkam.ui.setting

import android.os.Bundle
import android.view.View
import com.mulkkam.R
import com.mulkkam.databinding.FragmentSettingBinding
import com.mulkkam.ui.main.Refreshable
import com.mulkkam.ui.setting.adapter.SettingAdapter
import com.mulkkam.ui.setting.adapter.SettingItem
import com.mulkkam.ui.setting.model.SettingType
import com.mulkkam.ui.settingaccountinfo.SettingAccountInfoActivity
import com.mulkkam.ui.settingbioinfo.SettingBioInfoActivity
import com.mulkkam.ui.settingcups.SettingCupsActivity
import com.mulkkam.ui.settingnickname.SettingNicknameActivity
import com.mulkkam.ui.settingnotification.SettingNotificationActivity
import com.mulkkam.ui.settingtargetamount.SettingTargetAmountActivity
import com.mulkkam.ui.util.binding.BindingFragment

class SettingFragment :
    BindingFragment<FragmentSettingBinding>(FragmentSettingBinding::inflate),
    Refreshable {
    private val adapter by lazy { handleSettingClick() }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvSettingOptions.adapter = adapter
        initSettingItems()
    }

    private fun handleSettingClick() =
        SettingAdapter(
            object : SettingAdapter.Handler {
                override fun onSettingNormalClick(item: SettingItem.NormalItem) {
                    handleSettingNormalClick(item.type)
                }
            },
        )

    private fun handleSettingNormalClick(type: SettingType) {
        when (type) {
            SettingType.NICKNAME -> startActivity(SettingNicknameActivity.newIntent(requireContext()))
            SettingType.BODY_INFO -> startActivity(SettingBioInfoActivity.newIntent(requireContext()))
            SettingType.ACCOUNT_INFO -> startActivity(SettingAccountInfoActivity.newIntent(requireContext()))
            SettingType.MY_CUP -> startActivity(SettingCupsActivity.newIntent(requireContext()))
            SettingType.GOAL -> startActivity(SettingTargetAmountActivity.newIntent(requireContext()))
            SettingType.PUSH_NOTIFICATION -> startActivity(SettingNotificationActivity.newIntent(requireContext()))
            SettingType.FEEDBACK -> Unit // TODO
            SettingType.TERMS -> Unit // TODO
        }
    }

    private fun initSettingItems() {
        val settingItems =
            listOf(
                SettingItem.TitleItem(getString(R.string.setting_section_account)),
                SettingItem.NormalItem(getString(R.string.setting_nickname_edit_nickname_label), SettingType.NICKNAME),
                SettingItem.NormalItem(getString(R.string.setting_item_body_info), SettingType.BODY_INFO),
                SettingItem.NormalItem(getString(R.string.setting_item_account_info), SettingType.ACCOUNT_INFO),
                SettingItem.DividerItem,
                SettingItem.TitleItem(getString(R.string.setting_section_water)),
                SettingItem.NormalItem(getString(R.string.setting_cups_toolbar_title), SettingType.MY_CUP),
                SettingItem.NormalItem(getString(R.string.setting_target_amount_toolbar_title), SettingType.GOAL),
                SettingItem.DividerItem,
                SettingItem.TitleItem(getString(R.string.setting_section_notification)),
                SettingItem.NormalItem(getString(R.string.setting_item_push_notification), SettingType.PUSH_NOTIFICATION),
                SettingItem.DividerItem,
                SettingItem.TitleItem(getString(R.string.setting_section_service_center)),
                SettingItem.NormalItem(getString(R.string.setting_item_feedback), SettingType.FEEDBACK),
                SettingItem.NormalItem(getString(R.string.setting_item_terms), SettingType.TERMS),
            )
        adapter.submitList(settingItems)
    }
}
