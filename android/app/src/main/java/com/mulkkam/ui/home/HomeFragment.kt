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
import androidx.fragment.app.activityViewModels
import com.mulkkam.R
import com.mulkkam.domain.model.intake.IntakeInfo
import com.mulkkam.domain.model.intake.IntakeType
import com.mulkkam.domain.model.members.TodayProgressInfo
import com.mulkkam.domain.model.result.MulKkamError
import com.mulkkam.ui.custom.snackbar.CustomSnackBar
import com.mulkkam.ui.custom.toast.CustomToast
import com.mulkkam.ui.designsystem.MulkkamTheme
import com.mulkkam.ui.encyclopedia.CoffeeEncyclopediaActivity
import com.mulkkam.ui.home.dialog.ManualDrinkFragment
import com.mulkkam.ui.login.LoginActivity
import com.mulkkam.ui.main.MainActivity
import com.mulkkam.ui.main.Refreshable
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.notification.NotificationActivity
import com.mulkkam.ui.pendingfriends.PendingFriendsActivity
import com.mulkkam.ui.service.NotificationAction
import com.mulkkam.ui.util.extensions.collectWithLifecycle

class HomeFragment :
    Fragment(),
    Refreshable {
    private val viewModel: HomeViewModel by activityViewModels()
    private lateinit var notificationResultLauncher: ActivityResultLauncher<Intent>
    private var snackbarAnchorView: View? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val composeView =
            ComposeView(requireContext()).apply {
                setContent {
                    MulkkamTheme {
                        HomeScreen(
                            viewModel = viewModel,
                            navigateToNotification = {
                                val intent = NotificationActivity.newIntent(requireContext())
                                notificationResultLauncher.launch(intent)
                            },
                            onManualDrink = {
                                if (childFragmentManager.findFragmentByTag(ManualDrinkFragment.TAG) != null) return@HomeScreen
                                ManualDrinkFragment
                                    .newInstance()
                                    .show(childFragmentManager, ManualDrinkFragment.TAG)
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
        snackbarAnchorView = view
        initActivityResultLauncher()
        initCollectors()
    }

    private fun initActivityResultLauncher() {
        notificationResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    val isApply =
                        it.data?.getBooleanExtra(NotificationActivity.EXTRA_KEY_IS_APPLY, false)
                    viewModel.loadAlarmCount()
                    if (isApply == true) {
                        viewModel.loadTodayProgressInfo()
                    }
                }
            }
    }

    private fun initCollectors() {
        viewModel.todayProgressInfoUiState.collectWithLifecycle(this) { state ->
            handleTodayProgressFailures(state)
        }
        viewModel.drinkUiState.collectWithLifecycle(this) { state ->
            handleDrinkUiState(state)
        }
    }

    private fun handleTodayProgressFailures(state: MulKkamUiState<TodayProgressInfo>) {
        if (state is MulKkamUiState.Failure) {
            handleFailure(state.error)
        }
    }

    private fun handleDrinkUiState(state: MulKkamUiState<IntakeInfo>) {
        when (state) {
            is MulKkamUiState.Success ->
                showIntakeSuccessSnack(
                    intakeType = state.data.intakeType,
                    amount = state.data.amount,
                )

            is MulKkamUiState.Failure -> {
                showSnackBar(
                    message = getString(R.string.manual_drink_network_error),
                    iconRes = R.drawable.ic_alert_circle,
                )
            }

            MulKkamUiState.Idle,
            MulKkamUiState.Loading,
            -> Unit
        }
    }

    private fun showIntakeSuccessSnack(
        intakeType: IntakeType,
        amount: Int,
    ) {
        when (intakeType) {
            IntakeType.WATER -> {
                showSnackBar(
                    message = getString(R.string.manual_drink_success, amount),
                    iconRes = R.drawable.ic_terms_all_check_on,
                )
            }

            IntakeType.COFFEE -> {
                showSnackBar(
                    message = getString(R.string.manual_drink_success_coffee, amount),
                    iconRes = R.drawable.ic_terms_all_check_on,
                    action = {
                        val intent = CoffeeEncyclopediaActivity.newIntent(requireContext())
                        startActivity(intent)
                    },
                )
            }

            IntakeType.UNKNOWN -> Unit
        }
    }

    private fun handleFailure(error: MulKkamError) {
        if (error is MulKkamError.AccountError || error is MulKkamError.Unknown) {
            CustomToast
                .makeText(
                    requireContext(),
                    getString(R.string.authorization_expired),
                    R.drawable.ic_alert_circle,
                ).show()
            navigateToLogin()
        } else {
            showSnackBar(
                message = getString(R.string.load_info_error),
                iconRes = R.drawable.ic_alert_circle,
            )
        }
    }

    private fun showSnackBar(
        message: String,
        iconRes: Int,
        action: (() -> Unit)? = null,
    ) {
        val anchor = snackbarAnchorView ?: requireActivity().findViewById(android.R.id.content)
        CustomSnackBar
            .make(anchor, message, iconRes)
            .apply {
                setTranslationY(MainActivity.SNACK_BAR_BOTTOM_NAV_OFFSET)
                if (action != null) setAction(action)
            }.show()
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
