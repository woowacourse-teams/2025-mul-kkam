package com.mulkkam.ui.setting

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import com.mulkkam.R
import com.mulkkam.databinding.FragmentSettingBinding
import com.mulkkam.ui.main.Refreshable
import com.mulkkam.ui.setting.adapter.SettingAdapter
import com.mulkkam.ui.setting.adapter.SettingItem
import com.mulkkam.ui.setting.model.SettingType
import com.mulkkam.ui.settingaccountinfo.SettingAccountInfoActivity
import com.mulkkam.ui.settingbioinfo.SettingBioInfoActivity
import com.mulkkam.ui.settingcups.SettingCupsActivity
import com.mulkkam.ui.settingcups.model.CupUiModel
import com.mulkkam.ui.settingfeedback.SettingFeedbackActivity
import com.mulkkam.ui.settingnickname.SettingNicknameActivity
import com.mulkkam.ui.settingnotification.SettingNotificationActivity
import com.mulkkam.ui.settingreminder.SettingReminderActivity
import com.mulkkam.ui.settingtargetamount.SettingTargetAmountActivity
import com.mulkkam.ui.settingterms.SettingTermsActivity
import com.mulkkam.ui.util.binding.BindingFragment
import com.mulkkam.ui.util.extensions.getParcelableArrayListExtraCompat

class SettingFragment :
    BindingFragment<FragmentSettingBinding>(FragmentSettingBinding::inflate),
    Refreshable {
    private val adapter by lazy { handleSettingClick() }
    private val viewModel: SettingViewModel by viewModels()
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvSettingOptions.adapter = adapter
        initSettingItems()
        initActivityResultLauncher()
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
            SettingType.MY_CUP -> activityResultLauncher.launch(SettingCupsActivity.newIntent(requireContext()))
            SettingType.GOAL -> startActivity(SettingTargetAmountActivity.newIntent(requireContext()))
            SettingType.PUSH_NOTIFICATION -> startActivity(SettingNotificationActivity.newIntent(requireContext()))
            SettingType.FEEDBACK -> startActivity(SettingFeedbackActivity.newIntent(requireContext()))
            SettingType.TERMS -> startActivity(SettingTermsActivity.newIntent(requireContext()))
            SettingType.REMINDER -> startActivity(SettingReminderActivity.newIntent(requireContext()))
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
                SettingItem.NormalItem(getString(R.string.setting_reminder_toolbar_title), SettingType.REMINDER),
                SettingItem.DividerItem,
                SettingItem.TitleItem(getString(R.string.setting_section_support)),
                SettingItem.NormalItem(getString(R.string.setting_item_feedback), SettingType.FEEDBACK),
                SettingItem.NormalItem(getString(R.string.setting_item_terms), SettingType.TERMS),
            )
        adapter.submitList(settingItems)
    }

    private fun initActivityResultLauncher() {
        activityResultLauncher =
            registerForActivityResult(
                ActivityResultContracts.StartActivityForResult(),
            ) { result ->
                if (result.resultCode == RESULT_OK) {
                    val cups =
                        result.data?.getParcelableArrayListExtraCompat<CupUiModel>(
                            SettingCupsActivity.EXTRA_KEY_LATEST_CUPS_ORDER,
                        )
                    if (!cups.isNullOrEmpty()) {
                        viewModel.saveCupOrder(cups)
                    }
                }
            }
    }
}
