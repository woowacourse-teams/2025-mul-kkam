package com.mulkkam.ui.setting

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.mulkkam.ui.designsystem.MulkkamTheme
import com.mulkkam.ui.main.Refreshable
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
import com.mulkkam.ui.util.extensions.getParcelableArrayListExtraCompat

class SettingFragment :
    Fragment(),
    Refreshable {
    private val viewModel: SettingViewModel by viewModels()
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        initActivityResultLauncher()

        return ComposeView(requireContext()).also { composeView ->
            composeView.setContent {
                MulkkamTheme {
                    SettingScreen(
                        onSettingClick = { type -> handleSettingClick(type) },
                    )
                }
            }
        }
    }

    private fun handleSettingClick(type: SettingType) {
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
