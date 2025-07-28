package com.mulkkam.ui.history

import android.graphics.SweepGradient
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.annotation.ColorRes
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.toColorInt
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.mulkkam.R
import com.mulkkam.databinding.FragmentHistoryBinding
import com.mulkkam.databinding.HistoryWaterIntakeChartBinding
import com.mulkkam.domain.IntakeHistory
import com.mulkkam.domain.IntakeHistorySummary
import com.mulkkam.ui.binding.BindingFragment
import com.mulkkam.ui.history.adapter.HistoryAdapter
import com.mulkkam.ui.main.Refreshable
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.min

class HistoryFragment :
    BindingFragment<FragmentHistoryBinding>(
        FragmentHistoryBinding::inflate,
    ),
    Refreshable {
    private val viewModel: HistoryViewModel by viewModels()
    private val historyAdapter: HistoryAdapter by lazy { HistoryAdapter() }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initHighlight()
        initHistoryAdapter()
        initChartOptions()
        initCustomChartOptions()
        initObservers()
    }

    private fun initHighlight() {
        binding.tvViewSubLabel.text =
            getColoredSpannable(
                R.color.primary_200,
                getString(R.string.history_view_sub_label_prefix) + " " + getString(R.string.history_view_sub_label_suffix),
                getString(R.string.history_view_sub_label_suffix),
            )
    }

    private fun getColoredSpannable(
        @ColorRes colorResId: Int,
        fullText: String,
        vararg highlightedText: String,
    ): SpannableString {
        val color = requireContext().getColor(colorResId)
        val spannable = SpannableString(fullText)

        highlightedText.forEach { target ->
            var startIndex = fullText.indexOf(target)
            spannable.setSpan(
                ForegroundColorSpan(color),
                startIndex,
                startIndex + target.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE,
            )
        }

        return spannable
    }

    private fun initHistoryAdapter() {
        with(binding.rvIntakeHistory) {
            adapter = historyAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun initChartOptions() {
        val pieCharts =
            listOf(
                binding.includeChartMon.pcWaterIntake,
                binding.includeChartTue.pcWaterIntake,
                binding.includeChartWed.pcWaterIntake,
                binding.includeChartThu.pcWaterIntake,
                binding.includeChartFri.pcWaterIntake,
                binding.includeChartSat.pcWaterIntake,
                binding.includeChartSun.pcWaterIntake,
            )

        pieCharts.forEach { chart ->
            chart.apply {
                setPaintColor(R.color.primary_200)
                setStroke(DONUT_CHART_SOLID_STROKE)
                setBackgroundPaintColor(R.color.primary_50)
            }
        }
    }

    private fun initCustomChartOptions() {
        with(binding.viewDailyChart) {
            post {
                setPaintGradient(
                    createSweepGradient(
                        width,
                        height,
                    ),
                )
            }
            setStroke(DONUT_CHART_GRADIENT_STROKE)
            setBackgroundPaintColor(R.color.gray_10)
        }
    }

    private fun initObservers() {
        viewModel.weeklyIntakeHistories.observe(viewLifecycleOwner) { weeklyIntakeHistories ->
            bindWeeklyChartData(weeklyIntakeHistories)
        }

        viewModel.dailyIntakeHistories.observe(viewLifecycleOwner) { dailyIntakeHistories ->
            updateDailyChart(dailyIntakeHistories)
            updateIntakeHistories(dailyIntakeHistories.intakeHistories)
        }
    }

    private fun bindWeeklyChartData(weeklyIntakeHistories: List<IntakeHistorySummary>) {
        val pieCharts =
            listOf(
                binding.includeChartMon,
                binding.includeChartTue,
                binding.includeChartWed,
                binding.includeChartThu,
                binding.includeChartFri,
                binding.includeChartSat,
                binding.includeChartSun,
            )

        pieCharts.forEachIndexed { index, chart ->
            val intake = weeklyIntakeHistories[index]
            updateWeeklyChart(chart, intake)
        }
    }

    private fun updateWeeklyChart(
        chart: HistoryWaterIntakeChartBinding,
        intakeHistorySummary: IntakeHistorySummary,
    ) {
        chart.apply {
            tvWaterGoalRate.text = intakeHistorySummary.achievementRate.toInt().toString()
            // TODO: 한국어로 매핑 & 토/일 색깔 변경 필요
            tvDayOfWeek.text =
                intakeHistorySummary.date.dayOfWeek
                    .toString()
                    .substring(0..2)
            tvMonthDay.text =
                getString(
                    R.string.water_chart_date,
                    intakeHistorySummary.date.monthValue,
                    intakeHistorySummary.date.dayOfMonth,
                )
            // TODO: 클릭리스너 위치 변경 필요
            pcWaterIntake.setOnClickListener {
                viewModel.updateDailyIntakeHistories(intakeHistorySummary)
            }
            pcWaterIntake.setProgress(intakeHistorySummary.achievementRate)
        }
    }

    private fun updateDailyChart(intakeHistorySummary: IntakeHistorySummary) {
        with(binding) {
            viewDailyChart.setProgress(intakeHistorySummary.achievementRate)
            tvDailyChartLabel.text =
                getColoredSpannable(
                    R.color.primary_200,
                    getString(
                        R.string.history_daily_chart_label,
                        intakeHistorySummary.date.format(DATE_FORMATTER_KR),
                    ),
                    intakeHistorySummary.date.format(DATE_FORMATTER_KR),
                )
            updateDailyIntakeSummary(intakeHistorySummary)
        }
    }

    private fun updateDailyIntakeSummary(intakeHistorySummary: IntakeHistorySummary) {
        val formattedIntake =
            String.format(Locale.US, "%,dml", intakeHistorySummary.totalIntakeAmount)

        @ColorRes val summaryColorResId =
            if (intakeHistorySummary.targetAmount == 0) {
                R.color.gray_200
            } else {
                R.color.primary_200
            }
        binding.tvDailyIntakeSummary.text =
            getColoredSpannable(
                summaryColorResId,
                getString(
                    R.string.history_daily_intake_summary,
                    intakeHistorySummary.totalIntakeAmount,
                    intakeHistorySummary.targetAmount,
                ),
                formattedIntake,
            )
    }

    private fun updateIntakeHistories(intakeHistories: List<IntakeHistory>) {
        historyAdapter.changeItems(intakeHistories)
        binding.tvNoIntakeHistory.isVisible = intakeHistories.isEmpty()
    }

    private fun createSweepGradient(
        width: Int,
        height: Int,
    ): SweepGradient {
        val size = min(width, height).toFloat()

        return SweepGradient(
            size / 2,
            size / 2,
            intArrayOf(
                ColorUtils.setAlphaComponent("#FFB7A5".toColorInt(), (255 * 0.5f).toInt()),
                ColorUtils.setAlphaComponent(
                    "#FFEBDD".toColorInt(),
                    (255 * 0.75f).toInt(),
                ),
                "#C9F0F8".toColorInt(),
                ColorUtils.setAlphaComponent("#FFB7A5".toColorInt(), (255 * 0.5f).toInt()),
            ),
            floatArrayOf(
                0.0f,
                0.15f,
                0.70f,
                1.0f,
            ),
        )
    }

    override fun onReselected() {
        viewModel.loadIntakeHistories()
    }

    companion object {
        private val DATE_FORMATTER_KR: DateTimeFormatter =
            DateTimeFormatter.ofPattern("M월 d일 (E)", Locale.KOREAN)

        private const val DONUT_CHART_GRADIENT_STROKE: Float = 20f
        private const val DONUT_CHART_SOLID_STROKE: Float = 4f
    }
}
