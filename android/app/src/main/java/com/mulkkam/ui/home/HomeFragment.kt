package com.mulkkam.ui.home

import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.view.View
import androidx.annotation.ColorRes
import androidx.core.graphics.toColorInt
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.mulkkam.R
import com.mulkkam.databinding.FragmentHomeBinding
import com.mulkkam.domain.model.Cups
import com.mulkkam.domain.model.TodayProgressInfo
import com.mulkkam.ui.binding.BindingFragment
import com.mulkkam.ui.custom.ExtendableFloatingMenuItem
import com.mulkkam.ui.main.Refreshable
import com.mulkkam.ui.notification.NotificationActivity
import com.mulkkam.ui.util.getColoredSpannable
import java.util.Locale

class HomeFragment :
    BindingFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate),
    Refreshable {
    private val viewModel: HomeViewModel by viewModels()

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
        viewModel.todayProgressInfo.observe(viewLifecycleOwner) { progressInfo ->
            binding.pbHomeWaterProgress.setProgress(progressInfo.achievementRate)
            updateDailyProgressInfo(progressInfo)
        }

        viewModel.cups.observe(viewLifecycleOwner) { cups ->
            updateDrinkMenu(cups)
        }

        viewModel.characterChat.observe(viewLifecycleOwner) { chat ->
            binding.tvHomeCharacterChat.text = chat ?: return@observe
        }

        viewModel.alarmCount.observe(viewLifecycleOwner) { alarmCount ->
            binding.tvAlarmCount.text = alarmCount.toString()
            binding.tvAlarmCount.isVisible = alarmCount != ALARM_COUNT_MIN
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
                    ExtendableFloatingMenuItem(cup.nickname, cup.emoji, cup)
                } +
                    ExtendableFloatingMenuItem(
                        getString(R.string.home_drink_manual),
                        MANUAL_DRINK_IMAGE,
                    ),
            onItemClick = {
                // TODO: null 시 수동 입력 기능 추가
                viewModel.addWaterIntake(it.data?.id ?: return@setMenuItems)
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
        binding.ivHomeNotification.setOnClickListener {
            val intent = NotificationActivity.newIntent(requireContext())
            startActivity(intent)
        }
    }

    override fun onReselected() {
        viewModel.loadTodayProgressInfo()
        viewModel.loadCups()
    }

    companion object {
        private const val PROGRESS_BAR_RADIUS: Float = 12f
        private const val MANUAL_DRINK_IMAGE: String =
            "https://github-production-user-asset-6210df.s3.amazonaws.com/127238018/474919237-4e25a9f8-ab08-46e4-bd01-578d2de907df.svg?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=AKIAVCODYLSA53PQK4ZA%2F20250806%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20250806T085526Z&X-Amz-Expires=300&X-Amz-Signature=2c41117c496fdf0a94dd9062232cc396e7e44f58048958a92185c836d1caf5d4&X-Amz-SignedHeaders=host"
        private const val ALARM_COUNT_MIN: Int = 0
    }
}
