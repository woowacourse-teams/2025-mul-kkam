package com.mulkkam.ui.home

import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.view.View
import androidx.annotation.ColorRes
import androidx.core.graphics.toColorInt
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.google.android.material.snackbar.Snackbar
import com.mulkkam.R
import com.mulkkam.databinding.FragmentHomeBinding
import com.mulkkam.domain.model.Cups
import com.mulkkam.domain.model.TodayProgressInfo
import com.mulkkam.ui.binding.BindingFragment
import com.mulkkam.ui.custom.ExtendableFloatingMenuIcon
import com.mulkkam.ui.custom.ExtendableFloatingMenuItem
import com.mulkkam.ui.home.dialog.ManualDrinkFragment
import com.mulkkam.ui.main.Refreshable
import com.mulkkam.ui.notification.NotificationActivity
import com.mulkkam.ui.util.getColoredSpannable
import com.mulkkam.ui.util.setSingleClickListener
import java.util.Locale

class HomeFragment :
    BindingFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate),
    Refreshable {
    private val viewModel: HomeViewModel by activityViewModels()

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        initObservers()
        initCustomChartOptions()
        initClickListeners()
    }

    private fun initObservers() {
        with(viewModel) {
            todayProgressInfo.observe(viewLifecycleOwner) { progressInfo ->
                binding.pbHomeWaterProgress.setProgress(progressInfo.achievementRate)
                updateDailyProgressInfo(progressInfo)
            }

            cups.observe(viewLifecycleOwner) { cups ->
                updateDrinkMenu(cups)
            }

            characterChat.observe(viewLifecycleOwner) { chat ->
                binding.tvHomeCharacterChat.text = chat ?: return@observe
            }

            alarmCount.observe(viewLifecycleOwner) { alarmCount ->
                binding.tvAlarmCount.text = alarmCount.toString()
                binding.tvAlarmCount.isVisible = alarmCount != ALARM_COUNT_MIN
            }

            drinkSuccess.observe(viewLifecycleOwner) {
                Snackbar.make(binding.root, getString(R.string.manual_drink_success, it), Snackbar.LENGTH_SHORT).show()
            }
        }
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

    private fun updateDrinkMenu(cups: Cups) {
        binding.fabHomeDrink.setMenuItems(
            items =
                cups.cups.map { cup ->
                    ExtendableFloatingMenuItem(
                        label = cup.nickname,
                        icon = ExtendableFloatingMenuIcon.Url(cup.emoji),
                        data = cup,
                    )
                } +
                    ExtendableFloatingMenuItem(
                        label = getString(R.string.home_drink_manual),
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
            startActivity(intent)
        }
    }

    private fun showManualDrinkBottomSheetDialog() {
        if (childFragmentManager.findFragmentByTag(ManualDrinkFragment.TAG) != null) return
        ManualDrinkFragment
            .newInstance()
            .show(childFragmentManager, ManualDrinkFragment.TAG)
    }

    override fun onReselected() {
        viewModel.loadTodayProgressInfo()
        viewModel.loadCups()
        binding.fabHomeDrink.closeMenu()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadTodayProgressInfo()
    }

    companion object {
        private const val PROGRESS_BAR_RADIUS: Float = 12f
        private const val ALARM_COUNT_MIN: Int = 0
    }
}
