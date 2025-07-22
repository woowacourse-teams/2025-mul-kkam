package com.mulkkam.ui.record

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
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
import com.mulkkam.domain.DailyWaterIntake
import com.mulkkam.ui.binding.BindingFragment
import com.mulkkam.ui.main.Refreshable
import com.mulkkam.ui.record.adapter.RecordAdapter
import java.time.format.DateTimeFormatter
import java.util.Locale

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
        initRecordAdapter()
        initChartOptions()
        initObservers()
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
                holeRadius = HOLE_RADIUS
            }
        }
    }

    private fun initObservers() {
        viewModel.weeklyWaterIntake.observe(viewLifecycleOwner) { weeklyWaterIntake ->
            bindWeeklyChartData(weeklyWaterIntake)
        }

        viewModel.dailyWaterIntake.observe(viewLifecycleOwner) { dailyWaterIntake ->
            updateDailyWaterChart(dailyWaterIntake)
        }

        viewModel.dailyWaterRecords.observe(viewLifecycleOwner) { waterRecords ->
            recordAdapter.changeItems(waterRecords)
            binding.tvNoWaterRecord.isVisible = waterRecords.isEmpty()
        }
    }

    private fun bindWeeklyChartData(weeklyWaterIntake: List<DailyWaterIntake>) {
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
            val intake =
                weeklyWaterIntake.getOrNull(index)
                    ?: DailyWaterIntake.EMPTY_DAILY_WATER_INTAKE.copy(
                        date =
                            weeklyWaterIntake.first().date.plusDays(
                                index.toLong(),
                            ),
                    )
            updateWeeklyChart(chart, intake)
        }
    }

    private fun updateWeeklyChart(
        chart: RecordWaterIntakeChartBinding,
        intake: DailyWaterIntake,
    ) {
        chart.apply {
            tvWaterGoalRate.text = intake.goalRate.toInt().toString()
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
            pcWaterIntake.setOnClickListener {
                viewModel.updateDailyWaterIntake(intake)
            }
            updateChartData(pcWaterIntake, intake)
        }
    }

    private fun updateChartData(
        pieChart: PieChart,
        waterIntake: DailyWaterIntake,
    ) {
        pieChart.apply {
            data = createPieData(waterIntake.goalRate)
            animateY(ANIMATION_DURATION_MS, Easing.EaseInOutQuad)
            invalidate()
        }
    }

    private fun updateDailyWaterChart(dailyWaterIntake: DailyWaterIntake) {
        binding.viewDailyChart.setProgress(dailyWaterIntake.goalRate)
        binding.tvDailyWaterSummary.text =
            getString(
                R.string.record_daily_water_summary,
                dailyWaterIntake.intakeAmount,
                dailyWaterIntake.targetAmount,
            )
        binding.tvDailyChartLabel.text =
            getString(
                R.string.record_daily_chart_label,
                dailyWaterIntake.date.format(DATE_FORMATTER_KR),
            )
    }

    private fun createPieData(goalRate: Float): PieData {
        val entries =
            listOf(
                PieEntry(goalRate),
                PieEntry(MAX_PERCENTAGE - goalRate),
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

    companion object {
        private val DATE_FORMATTER_KR: DateTimeFormatter =
            DateTimeFormatter.ofPattern("M월 d일 (E)", Locale.KOREAN)

        private const val MAX_PERCENTAGE: Float = 100f
        private const val ANIMATION_DURATION_MS: Int = 1000
        private const val HOLE_RADIUS: Float = 60f
    }
}
