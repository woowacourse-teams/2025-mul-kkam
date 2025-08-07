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
import com.mulkkam.domain.IntakeHistorySummary
import com.mulkkam.domain.model.Cups
import com.mulkkam.ui.binding.BindingFragment
import com.mulkkam.ui.custom.ExtendableFloatingMenuItem
import com.mulkkam.ui.main.Refreshable
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
    }

    private fun initObservers() {
        viewModel.todayIntakeHistorySummary.observe(viewLifecycleOwner) { summary ->
            binding.pbHomeWaterProgress.setProgress(summary.achievementRate)
            updateDailyIntakeSummary(summary)
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

    private fun updateDailyIntakeSummary(intakeHistorySummary: IntakeHistorySummary) {
        val formattedIntake =
            String.format(Locale.US, "%,dml", intakeHistorySummary.totalIntakeAmount)

        @ColorRes val summaryColorResId =
            if (intakeHistorySummary.targetAmount > intakeHistorySummary.totalIntakeAmount) {
                R.color.gray_200
            } else {
                R.color.primary_200
            }
        binding.tvDailyIntakeSummary.text =
            getString(
                R.string.home_daily_intake_summary,
                intakeHistorySummary.totalIntakeAmount,
                intakeHistorySummary.targetAmount,
            ).getColoredSpannable(
                requireContext(),
                summaryColorResId,
                formattedIntake,
            )
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

    override fun onReselected() {
        viewModel.loadTodayIntakeHistorySummary()
        viewModel.loadCups()
    }

    companion object {
        private const val PROGRESS_BAR_RADIUS: Float = 12f
        private const val MANUAL_DRINK_IMAGE: String =
            "https://github-production-user-asset-6210df.s3.amazonaws.com/127238018/474919237-4e25a9f8-ab08-46e4-bd01-578d2de907df.svg?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=AKIAVCODYLSA53PQK4ZA%2F20250806%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20250806T085526Z&X-Amz-Expires=300&X-Amz-Signature=2c41117c496fdf0a94dd9062232cc396e7e44f58048958a92185c836d1caf5d4&X-Amz-SignedHeaders=host"
        private const val ALARM_COUNT_MIN: Int = 0
    }
}
