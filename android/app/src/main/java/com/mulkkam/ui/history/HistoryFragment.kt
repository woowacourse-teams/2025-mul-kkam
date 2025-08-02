package com.mulkkam.ui.history

import android.content.res.ColorStateList
import android.graphics.SweepGradient
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat.getColor
import androidx.core.content.ContextCompat.getDrawable
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.toColorInt
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.mulkkam.R
import com.mulkkam.databinding.FragmentHistoryBinding
import com.mulkkam.databinding.HistoryWaterIntakeChartBinding
import com.mulkkam.domain.IntakeHistory
import com.mulkkam.domain.IntakeHistorySummaries
import com.mulkkam.domain.IntakeHistorySummary
import com.mulkkam.ui.binding.BindingFragment
import com.mulkkam.ui.history.adapter.HistoryAdapter
import com.mulkkam.ui.main.Refreshable
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import kotlin.math.min

class HistoryFragment :
    BindingFragment<FragmentHistoryBinding>(
        FragmentHistoryBinding::inflate,
    ),
    Refreshable {
    private val viewModel: HistoryViewModel by viewModels()
    private val historyAdapter: HistoryAdapter by lazy { HistoryAdapter() }
    private var selectedChartBinding: HistoryWaterIntakeChartBinding? = null
    private val weeklyCharts: List<HistoryWaterIntakeChartBinding> by lazy {
        listOf(
            binding.includeChartMon,
            binding.includeChartTue,
            binding.includeChartWed,
            binding.includeChartThu,
            binding.includeChartFri,
            binding.includeChartSat,
            binding.includeChartSun,
        )
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initHistoryAdapter()
        initChartOptions()
        initCustomChartOptions()
        initObservers()
        initClickListeners()
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
        val pieCharts = weeklyCharts.map { it.pcWaterIntake }

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
            updateWeeklyChartHighlight(dailyIntakeHistories.dayOfWeekIndex())
        }

        viewModel.hasLog.observe(viewLifecycleOwner) { hasLog ->
            if (!hasLog) {
                binding.viewDailyChart.visibility = View.INVISIBLE
                binding.ivHistoryCharacter.setImageDrawable(
                    getDrawable(
                        requireContext(),
                        R.drawable.img_history_crying_character,
                    ),
                )
            } else {
                binding.viewDailyChart.isVisible = true
                binding.ivHistoryCharacter.setImageDrawable(
                    getDrawable(
                        requireContext(),
                        R.drawable.img_history_character,
                    ),
                )
            }
        }

        viewModel.isCurrentWeek.observe(viewLifecycleOwner) { isNextWeekAvailable ->
            binding.ibWeekNext.isVisible = isNextWeekAvailable
        }

        viewModel.isToday.observe(viewLifecycleOwner) { isToday ->
            binding.tvTodayLabel.isVisible = isToday
        }

        viewModel.isFuture.observe(viewLifecycleOwner) { isFuture ->
            binding.tvDailyIntakeSummary.isVisible = !isFuture
        }
    }

    private fun bindWeeklyChartData(weeklyIntakeHistories: IntakeHistorySummaries) {
        weeklyCharts.forEachIndexed { index, chart ->
            val intake = weeklyIntakeHistories.getByIndex(index)
            updateWeeklyChart(chart, intake)
        }

        val formatter =
            if (weeklyIntakeHistories.isCurrentYear) FORMATTER_MONTH_DATE else FORMATTER_FULL_DATE

        binding.tvWeekRange.text =
            getString(
                R.string.history_week_range,
                weeklyIntakeHistories.firstDay.format(formatter),
                weeklyIntakeHistories.lastDay.format(formatter),
            )
    }

    private fun updateWeeklyChart(
        chart: HistoryWaterIntakeChartBinding,
        intakeHistorySummary: IntakeHistorySummary,
    ) {
        chart.apply {
            root.setOnClickListener {
                viewModel.updateDailyIntakeHistories(intakeHistorySummary)
            }
            if (intakeHistorySummary.achievementRate == 100f) {
                tvWaterGoalRate.visibility = View.GONE
                ivCheck.visibility = View.VISIBLE
            } else {
                ivCheck.visibility = View.GONE
                tvWaterGoalRate.visibility = View.VISIBLE
                tvWaterGoalRate.text = intakeHistorySummary.achievementRate.toInt().toString()
            }

            tvDayOfWeek.text =
                intakeHistorySummary.date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.KOREAN)
            tvDayOfWeek.setTextColor(getColorByDate(intakeHistorySummary.date))
            tvMonthDay.text =
                getString(
                    R.string.water_chart_date,
                    intakeHistorySummary.date.monthValue,
                    intakeHistorySummary.date.dayOfMonth,
                )
            pcWaterIntake.setProgress(intakeHistorySummary.achievementRate)
        }
    }

    private fun getColorByDate(date: LocalDate): Int {
        val colorResId =
            when (date.dayOfWeek) {
                DayOfWeek.SATURDAY -> R.color.primary_300
                DayOfWeek.SUNDAY -> R.color.secondary_200
                else -> R.color.gray_400
            }
        return getColor(requireContext(), colorResId)
    }

    private fun updateDailyChart(intakeHistorySummary: IntakeHistorySummary) {
        with(binding) {
            viewDailyChart.setProgress(intakeHistorySummary.achievementRate)
            tvDailyChartLabel.text =
                getColoredSpannable(
                    R.color.primary_200,
                    getString(
                        R.string.history_daily_chart_label,
                        intakeHistorySummary.date.format(FORMATTER_DATE_WITH_DAY),
                    ),
                    intakeHistorySummary.date.format(FORMATTER_DATE_WITH_DAY),
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

    private fun updateWeeklyChartHighlight(dayOfWeek: Int) {
        selectedChartBinding?.let { resetChartHighlight(it) }

        val newChart = weeklyCharts.getOrNull(dayOfWeek) ?: return
        applyChartHighlight(newChart)
        selectedChartBinding = newChart
    }

    private fun resetChartHighlight(chart: HistoryWaterIntakeChartBinding) {
        chart.root.background = getDrawable(requireContext(), R.drawable.bg_common_rectangle_4dp)
        ViewCompat.setBackgroundTintList(
            chart.root,
            ColorStateList.valueOf(getColor(requireContext(), R.color.gray_10)),
        )
    }

    private fun applyChartHighlight(chart: HistoryWaterIntakeChartBinding) {
        chart.root.background =
            getDrawable(requireContext(), R.drawable.bg_common_rectangle_stroke_4dp)
        ViewCompat.setBackgroundTintList(chart.root, null)
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

    private fun initClickListeners() {
        binding.ibWeekPrev.setOnClickListener {
            viewModel.moveWeek(WEEK_OFFSET_PREV)
        }

        binding.ibWeekNext.setOnClickListener {
            viewModel.moveWeek(WEEK_OFFSET_NEXT)
        }
    }

    override fun onReselected() {
        viewModel.loadIntakeHistories()
    }

    companion object {
        private val FORMATTER_DATE_WITH_DAY: DateTimeFormatter =
            DateTimeFormatter.ofPattern("M월 d일 (E)", Locale.KOREAN)
        private val FORMATTER_MONTH_DATE: DateTimeFormatter =
            DateTimeFormatter.ofPattern("M월 d일")
        private val FORMATTER_FULL_DATE: DateTimeFormatter =
            DateTimeFormatter.ofPattern("yyyy년 M월 d일")

        private const val WEEK_OFFSET_PREV: Long = -1L
        private const val WEEK_OFFSET_NEXT: Long = 1L

        private const val DONUT_CHART_GRADIENT_STROKE: Float = 20f
        private const val DONUT_CHART_SOLID_STROKE: Float = 4f
    }
}
