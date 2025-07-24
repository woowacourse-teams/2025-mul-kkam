package com.mulkkam.ui.record

import android.graphics.SweepGradient
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.toColorInt
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.mulkkam.R
import com.mulkkam.databinding.FragmentRecordBinding
import com.mulkkam.databinding.RecordWaterIntakeChartBinding
import com.mulkkam.domain.IntakeHistorySummary
import com.mulkkam.ui.binding.BindingFragment
import com.mulkkam.ui.main.Refreshable
import com.mulkkam.ui.record.adapter.RecordAdapter
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.min

class RecordFragment :
    BindingFragment<FragmentRecordBinding>(
        FragmentRecordBinding::inflate,
    ),
    Refreshable {
    private val viewModel: RecordViewModel by viewModels()
    private val recordAdapter: RecordAdapter by lazy { RecordAdapter() }

    override fun onSelected() {
        // TODO: 화면 전환 시 필요한 작업을 구현합니다.
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initHighlight()
        initRecordAdapter()
        initChartOptions()
        initCustomChartOptions()
        initObservers()
    }

    private fun initHighlight() {
        binding.tvViewSubLabel.text =
            getColoredSpannable(
                R.color.primary_200,
                getString(R.string.record_view_sub_label_prefix) + " " + getString(R.string.record_view_sub_label_suffix),
                getString(R.string.record_view_sub_label_suffix),
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

    private fun initRecordAdapter() {
        with(binding.rvWaterRecord) {
            adapter = recordAdapter
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
                description.isEnabled = false
                legend.isEnabled = false
                setTouchEnabled(false)
                holeRadius = CHART_HOLE_RADIUS
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
            setStroke(DONUT_CHART_STROKE_DEFAULT)
            setBackgroundPaintColor(R.color.gray_10)
        }
    }

    private fun initObservers() {
        viewModel.weeklyWaterIntake.observe(viewLifecycleOwner) { weeklyWaterIntake ->
            bindWeeklyChartData(weeklyWaterIntake)
        }

        viewModel.dailyWaterIntake.observe(viewLifecycleOwner) { dailyWaterIntake ->
            bindDailyWaterChart(dailyWaterIntake)
        }

        viewModel.dailyWaterRecords.observe(viewLifecycleOwner) { waterRecords ->
            recordAdapter.changeItems(waterRecords)
            binding.tvNoWaterRecord.isVisible = waterRecords.isEmpty()
        }
    }

    private fun bindWeeklyChartData(weeklyWaterIntake: List<IntakeHistorySummary>) {
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
            val intake = weeklyWaterIntake[index]
            updateWeeklyChart(chart, intake)
        }
    }

    private fun updateWeeklyChart(
        chart: RecordWaterIntakeChartBinding,
        intake: IntakeHistorySummary,
    ) {
        chart.apply {
            tvWaterGoalRate.text = intake.achievementRate.toInt().toString()
            // TODO: 한국어로 매핑 & 토/일 색깔 변경 필요
            tvDayOfWeek.text =
                intake.date.dayOfWeek
                    .toString()
                    .substring(0..2)
            tvMonthDay.text =
                getString(
                    R.string.water_chart_date,
                    intake.date.monthValue,
                    intake.date.dayOfMonth,
                )
            // TODO: 클릭리스너 위치 변경 필요
            pcWaterIntake.setOnClickListener {
                viewModel.updateDailyWaterIntake(intake)
            }
            updateChartData(pcWaterIntake, intake)
        }
    }

    private fun updateChartData(
        pieChart: PieChart,
        waterIntake: IntakeHistorySummary,
    ) {
        pieChart.apply {
            data = createPieData(waterIntake.achievementRate)
            animateY(CHART_ANIMATION_DURATION_MS, Easing.EaseInOutQuad)
            invalidate()
        }
    }

    private fun createPieData(goalRate: Float): PieData {
        val entries =
            listOf(
                PieEntry(goalRate),
                PieEntry(CHART_MAX_PERCENTAGE - goalRate),
            )

        val colors =
            listOf(
                ContextCompat.getColor(requireContext(), R.color.primary_200),
                ContextCompat.getColor(requireContext(), R.color.primary_50),
            )

        val dataSet =
            PieDataSet(entries, "").apply {
                this.colors = colors
                setDrawValues(false)
            }

        return PieData(dataSet)
    }

    private fun bindDailyWaterChart(dailyWaterIntake: IntakeHistorySummary) {
        with(binding) {
            viewDailyChart.setProgress(dailyWaterIntake.achievementRate)
            val formattedIntake =
                String.format(Locale.US, "%,dml", dailyWaterIntake.totalIntakeAmount)
            tvDailyWaterSummary.text =
                getColoredSpannable(
                    R.color.primary_200,
                    getString(
                        R.string.record_daily_water_summary,
                        dailyWaterIntake.totalIntakeAmount,
                        dailyWaterIntake.targetAmount,
                    ),
                    formattedIntake,
                )
            tvDailyChartLabel.text =
                getColoredSpannable(
                    R.color.primary_200,
                    getString(
                        R.string.record_daily_chart_label,
                        dailyWaterIntake.date.format(DATE_FORMATTER_KR),
                    ),
                    dailyWaterIntake.date.format(DATE_FORMATTER_KR),
                )
        }
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

    companion object {
        private val DATE_FORMATTER_KR: DateTimeFormatter =
            DateTimeFormatter.ofPattern("M월 d일 (E)", Locale.KOREAN)

        private const val CHART_MAX_PERCENTAGE: Float = 100f
        private const val CHART_ANIMATION_DURATION_MS: Int = 1000
        private const val CHART_HOLE_RADIUS: Float = 80f

        private const val DONUT_CHART_STROKE_DEFAULT: Float = 20f
    }
}
