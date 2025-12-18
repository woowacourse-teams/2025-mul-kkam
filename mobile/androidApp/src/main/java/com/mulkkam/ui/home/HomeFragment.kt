package com.mulkkam.ui.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.mulkkam.ui.designsystem.MulkkamTheme
import com.mulkkam.ui.encyclopedia.CoffeeEncyclopediaActivity
import com.mulkkam.ui.login.LoginActivity
import com.mulkkam.ui.main.MainViewModel
import com.mulkkam.ui.main.Refreshable
import com.mulkkam.ui.notification.NotificationActivity
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class HomeFragment :
    Fragment(),
    Refreshable {
    private val viewModel: HomeViewModel by activityViewModel()
    private val parentViewModel: MainViewModel by activityViewModel()
    private lateinit var notificationResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val composeView =
            ComposeView(requireContext()).apply {
                setContent {
                    MulkkamTheme {
                        HomeRoute(
                            viewModel = viewModel,
                            parentViewModel = parentViewModel,
                            navigateToNotification = {
                                val intent = NotificationActivity.newIntent(requireContext())
                                notificationResultLauncher.launch(intent)
                            },
                            onNavigateToLogin = { navigateToLogin() },
                            onNavigateToCoffeeEncyclopedia = {
                                val intent = CoffeeEncyclopediaActivity.newIntent(requireContext())
                                startActivity(intent)
                            },
                        )
                    }
                }
            }
        return composeView
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initActivityResultLauncher()
    }

    private fun initActivityResultLauncher() {
        notificationResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val isApply =
                        result.data?.getBooleanExtra(NotificationActivity.EXTRA_KEY_IS_APPLY, false)
                    viewModel.loadAlarmCount()
                    if (isApply == true) {
                        viewModel.loadTodayProgressInfo()
                    }
                }
            }
    }

    private fun navigateToLogin() {
        val intent =
            LoginActivity.newIntent(requireContext()).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        startActivity(intent)
    }

    override fun onReselected() {
        viewModel.loadTodayProgressInfo()
        viewModel.loadCups()
        viewModel.loadAlarmCount()
    }
}
