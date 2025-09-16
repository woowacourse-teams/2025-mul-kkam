package com.mulkkam.ui.home

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.ColorRes
import androidx.core.graphics.toColorInt
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.mulkkam.R
import com.mulkkam.databinding.FragmentHomeBinding
import com.mulkkam.domain.model.cups.Cups
import com.mulkkam.domain.model.members.TodayProgressInfo
import com.mulkkam.domain.model.result.MulKkamError
import com.mulkkam.ui.custom.floatingactionbutton.ExtendableFloatingMenuIcon
import com.mulkkam.ui.custom.floatingactionbutton.ExtendableFloatingMenuItem
import com.mulkkam.ui.custom.snackbar.CustomSnackBar
import com.mulkkam.ui.custom.toast.CustomToast
import com.mulkkam.ui.home.dialog.ManualDrinkFragment
import com.mulkkam.ui.login.LoginActivity
import com.mulkkam.ui.main.MainActivity
import com.mulkkam.ui.main.Refreshable
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.notification.NotificationActivity
import com.mulkkam.ui.util.binding.BindingFragment
import com.mulkkam.ui.util.extensions.getColoredSpannable
import com.mulkkam.ui.util.extensions.setSingleClickListener
import java.util.Locale

class HomeFragment :
    BindingFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate),
    Refreshable {
    private val viewModel: HomeViewModel by activityViewModels()
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private val resetCharacterRunnable =
        Runnable {
            binding.ivHomeCharacter.setImageResource(R.drawable.img_home_character)
        }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        initObservers()
        initCustomChartOptions()
        initClickListeners()
        initActivityResultLauncher()
    }

    private fun initObservers() {
        with(viewModel) {
            todayProgressInfoUiState.observe(viewLifecycleOwner) { todayProgressInfoUiState ->
                handleTodayProgressInfo(todayProgressInfoUiState)
            }

            cupsUiState.observe(viewLifecycleOwner) { cupsUiState ->
                handleCupsUiState(cupsUiState)
            }

            alarmCountUiState.observe(viewLifecycleOwner) { alarmCountUiState ->
                handleAlarmCount(alarmCountUiState)
            }

            drinkUiState.observe(viewLifecycleOwner) { drinkUiState ->
                handleDrinkResult(drinkUiState)
            }

            isGoalAchieved.observe(viewLifecycleOwner) {
                binding.lottieConfetti.setAnimation(R.raw.lottie_home_confetti)
                binding.lottieConfetti.playAnimation()
            }
        }
    }

    private fun handleTodayProgressInfo(todayProgressInfoMulKkamUiState: MulKkamUiState<TodayProgressInfo>) {
        when (todayProgressInfoMulKkamUiState) {
            is MulKkamUiState.Success<TodayProgressInfo> ->
                showTodayProgressInfo(
                    todayProgressInfoMulKkamUiState,
                )

            is MulKkamUiState.Loading -> Unit
            is MulKkamUiState.Idle -> Unit
            is MulKkamUiState.Failure -> {
                handleFailure(todayProgressInfoMulKkamUiState.error)
            }
        }
    }

    private fun showTodayProgressInfo(todayProgressInfoMulKkamUiState: MulKkamUiState.Success<TodayProgressInfo>) {
        binding.pbHomeWaterProgress.setProgress(todayProgressInfoMulKkamUiState.data.achievementRate)
        binding.tvHomeCharacterChat.text = todayProgressInfoMulKkamUiState.data.comment
        updateDailyProgressInfo(todayProgressInfoMulKkamUiState.data)
    }

    private fun updateDailyProgressInfo(progressInfo: TodayProgressInfo) {
        updateDailyIntakeSummary(progressInfo.targetAmount, progressInfo.totalAmount)
        updateStreakMessage(progressInfo.nickname, progressInfo.streak)
        updateCharacterComment(progressInfo.comment)
    }

    private fun updateDailyIntakeSummary(
        targetAmount: Int,
        totalAmount: Int,
    ) {
        val formattedIntake = String.format(Locale.US, "%,dml", totalAmount)

        @ColorRes val summaryColorResId =
            if (targetAmount > totalAmount) {
                R.color.gray_200
            } else {
                R.color.primary_200
            }

        binding.tvDailyIntakeSummary.text =
            getString(
                R.string.home_daily_intake_summary,
                totalAmount,
                targetAmount,
            ).getColoredSpannable(
                requireContext(),
                summaryColorResId,
                formattedIntake,
            )
    }

    private fun updateStreakMessage(
        nickname: String,
        streak: Int,
    ) {
        binding.tvStreak.text =
            getString(
                R.string.home_water_streak_message,
                nickname,
                streak,
            ).getColoredSpannable(
                requireContext(),
                R.color.primary_200,
                nickname,
                streak.toString(),
            )
    }

    private fun updateCharacterComment(comment: String) {
        binding.tvHomeCharacterChat.text = comment
    }

    private fun handleFailure(error: MulKkamError) {
        if (error is MulKkamError.AccountError ||
            error is MulKkamError.Unknown
        ) {
            CustomToast
                .makeText(
                    requireContext(),
                    getString(R.string.authorization_expired),
                    R.drawable.ic_alert_circle,
                ).show()
            navigateToLogin()
        } else {
            CustomSnackBar
                .make(
                    binding.root,
                    getString(R.string.load_info_error),
                    R.drawable.ic_alert_circle,
                ).apply {
                    setTranslationY(MainActivity.SNACK_BAR_BOTTOM_NAV_OFFSET)
                }.show()
        }
    }

    private fun navigateToLogin() {
        val intent =
            LoginActivity.newIntent(requireContext()).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        startActivity(intent)
    }

    private fun handleCupsUiState(cupsUiState: MulKkamUiState<Cups>) {
        when (cupsUiState) {
            is MulKkamUiState.Success<Cups> -> updateDrinkOptions(cupsUiState.data)
            is MulKkamUiState.Idle -> Unit
            is MulKkamUiState.Loading -> Unit
            is MulKkamUiState.Failure -> Unit
        }
    }

    private fun updateDrinkOptions(cups: Cups) {
        binding.fabHomeDrink.setMenuItems(
            items =
                cups.cups.map { cup ->
                    ExtendableFloatingMenuItem(
                        buttonLabel = cup.name.value,
                        icon = ExtendableFloatingMenuIcon.Url(cup.emoji.cupEmojiUrl),
                        iconLabel =
                            getString(
                                R.string.expandable_floating_menu_intake_unit,
                                cup.amount.value,
                            ),
                        data = cup,
                    )
                } +
                    ExtendableFloatingMenuItem(
                        buttonLabel = getString(R.string.home_drink_manual),
                        icon = ExtendableFloatingMenuIcon.Resource(R.drawable.ic_manual_drink),
                        data = null,
                    ),
            onItemClick = {
                if (it.data == null) {
                    showManualDrinkBottomSheetDialog()
                } else {
                    viewModel.addWaterIntakeByCup(it.data.id)
                }
            },
        )
    }

    private fun initCustomChartOptions() {
        with(binding.pbHomeWaterProgress) {
            post {
                setPaintGradient(createLinearGradient(width.toFloat()))
            }
            setBackgroundPaintColor(R.color.white)
            setCornerRadius(PROGRESS_BAR_RADIUS)
        }
    }

    private fun createLinearGradient(width: Float): LinearGradient =
        LinearGradient(
            0f,
            0f,
            width,
            0f,
            intArrayOf(
                "#FFB7A5".toColorInt(),
                "#FFEBDD".toColorInt(),
                "#C9F0F8".toColorInt(),
                "#C9F0F8".toColorInt(),
            ),
            floatArrayOf(
                0.0f,
                0.15f,
                0.70f,
                1.0f,
            ),
            Shader.TileMode.CLAMP,
        )

    private fun initClickListeners() {
        binding.ivHomeNotification.setSingleClickListener {
            val intent = NotificationActivity.newIntent(requireContext())
            activityResultLauncher.launch(intent)
        }
    }

    private fun showManualDrinkBottomSheetDialog() {
        if (childFragmentManager.findFragmentByTag(ManualDrinkFragment.TAG) != null) return
        ManualDrinkFragment
            .newInstance()
            .show(childFragmentManager, ManualDrinkFragment.TAG)
    }

    private fun handleAlarmCount(alarmCountUiState: MulKkamUiState<Long>) {
        when (alarmCountUiState) {
            is MulKkamUiState.Success<Long> -> showAlarmCount(alarmCountUiState.data)
            MulKkamUiState.Idle -> showAlarmCount(ALARM_COUNT_MIN)
            MulKkamUiState.Loading -> Unit
            is MulKkamUiState.Failure -> Unit
        }
    }

    private fun showAlarmCount(count: Long) {
        binding.tvAlarmCount.text = count.toString()
        binding.tvAlarmCount.isVisible = count != ALARM_COUNT_MIN
    }

    private fun handleDrinkResult(drinkUiState: MulKkamUiState<Int>) {
        when (drinkUiState) {
            is MulKkamUiState.Success<Int> -> {
                CustomSnackBar
                    .make(
                        binding.root,
                        getString(
                            R.string.manual_drink_success,
                            drinkUiState.data,
                        ),
                        R.drawable.ic_terms_all_check_on,
                    ).apply {
                        setTranslationY(MainActivity.SNACK_BAR_BOTTOM_NAV_OFFSET)
                    }.show()
                binding.ivHomeCharacter.removeCallbacks(resetCharacterRunnable)
                binding.ivHomeCharacter.setImageResource(R.drawable.img_home_drink_character)
                binding.ivHomeCharacter.postDelayed(resetCharacterRunnable, 2000)
            }

            is MulKkamUiState.Idle -> Unit
            is MulKkamUiState.Loading -> Unit

            is MulKkamUiState.Failure -> {
                CustomSnackBar
                    .make(
                        binding.root,
                        getString(R.string.manual_drink_network_error),
                        R.drawable.ic_alert_circle,
                    ).apply {
                        setTranslationY(MainActivity.SNACK_BAR_BOTTOM_NAV_OFFSET)
                    }.show()
            }
        }
    }

    private fun initActivityResultLauncher() {
        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == RESULT_OK) {
                    val isApply =
                        it.data?.getBooleanExtra(NotificationActivity.EXTRA_KEY_IS_APPLY, false)
                    viewModel.loadAlarmCount()
                    if (isApply == true) viewModel.loadTodayProgressInfo()
                }
            }
    }

    override fun onReselected() {
        viewModel.loadTodayProgressInfo()
        viewModel.loadCups()
        viewModel.loadAlarmCount()
        binding.fabHomeDrink.closeMenu()
    }

    companion object {
        private const val PROGRESS_BAR_RADIUS: Float = 12f
        private const val ALARM_COUNT_MIN: Long = 0L
    }
}
