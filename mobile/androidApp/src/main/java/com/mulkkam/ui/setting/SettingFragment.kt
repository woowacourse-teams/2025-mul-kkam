package com.mulkkam.ui.setting

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.main.Refreshable
import com.mulkkam.ui.setting.setting.SettingScreen
import com.mulkkam.ui.setting.setting.model.SettingType
import com.mulkkam.ui.settingaccountinfo.SettingAccountInfoActivity
import com.mulkkam.ui.settingcups.SettingCupsActivity
import com.mulkkam.ui.settingfeedback.SettingFeedbackActivity
import com.mulkkam.ui.settingnotification.SettingNotificationActivity
import com.mulkkam.ui.settingreminder.SettingReminderActivity
import com.mulkkam.ui.settingterms.SettingTermsActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingFragment :
    Fragment(),
    Refreshable {
    private val viewModel: SettingViewModel by viewModel()
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        initActivityResultLauncher()

        return ComposeView(requireContext()).also { composeView ->
            composeView.setContent {
                MulKkamTheme {
                    SettingScreen(
                        padding = PaddingValues(0.dp),
                        navigateToSettingType = { type -> handleSettingClick(type) },
                    )
                }
            }
        }
    }

    private fun handleSettingClick(type: SettingType) {
        when (type) {
            SettingType.NICKNAME -> { /* SettingNicknameActivity migration completed */ }
            SettingType.BODY_INFO -> { /* SettingBioInfoActivity migration completed */ }
            SettingType.ACCOUNT_INFO -> startActivity(SettingAccountInfoActivity.newIntent(requireContext()))
            SettingType.MY_CUP -> activityResultLauncher.launch(SettingCupsActivity.newIntent(requireContext()))
            SettingType.GOAL -> { /* SettingTargetAmountActivity migration completed */ }
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
                // TODO: 해당 화면 마이그레이션 작업시 참고

                    /*
                    if (result.resultCode == RESULT_OK) {
                        val cups =
                            result.data?.getParcelableArrayListExtraCompat<CupUiModel>(
                                SettingCupsActivity.EXTRA_KEY_LATEST_CUPS_ORDER,
                            )
                        if (!cups.isNullOrEmpty()) {
                            viewModel.saveCupOrder(cups)
                        }
                    }
                     */
            }
    }
}
