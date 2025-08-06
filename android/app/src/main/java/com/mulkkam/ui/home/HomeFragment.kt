package com.mulkkam.ui.home

import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.view.View
import androidx.annotation.ColorRes
import androidx.core.graphics.toColorInt
import androidx.fragment.app.viewModels
import com.mulkkam.R
import com.mulkkam.databinding.FragmentHomeBinding
import com.mulkkam.di.LoggingInjection.logger
import com.mulkkam.domain.IntakeHistorySummary
import com.mulkkam.domain.model.LogEvent
import com.mulkkam.ui.binding.BindingFragment
import com.mulkkam.ui.custom.ExtendableFloatingMenuItem
import com.mulkkam.ui.main.Refreshable
import com.mulkkam.ui.settingwater.model.CupUiModel
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

        binding.fabHomeDrink.setOnClickListener {
            viewModel.addWaterIntake(cupRank = 1)
        }

        binding.fabHomeDrink.setMenuItems(
            listOf<ExtendableFloatingMenuItem<CupUiModel>>(
                ExtendableFloatingMenuItem(
                    label = "Kou",
                    iconUrl = "https://www.svgrepo.com/show/532086/water-arrow-up.svg",
                ),
                ExtendableFloatingMenuItem(
                    label = "Kong",
                    iconUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/4/47/PNG_transparency_demonstration_1.png/560px-PNG_transparency_demonstration_1.png",
                ),
                ExtendableFloatingMenuItem(
                    label = "Keren",
                    iconUrl = "https://github.com/user-attachments/assets/b1d372d8-08c2-41d5-ac06-9a3462f032ab",
                ),
            ),
            { item ->
                logger.debug(LogEvent.DEBUG, "HomeFragment", "Selected menu item: $item")
            },
        )
    }

    private fun initObservers() {
        viewModel.todayIntakeHistorySummary.observe(viewLifecycleOwner) { summary ->
            with(binding) {
                pbHomeWaterProgress.setProgress(summary.achievementRate)
            }
            updateDailyIntakeSummary(summary)
        }
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
    }
}
