package com.mulkkam.ui.record

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.mulkkam.R
import com.mulkkam.databinding.FragmentRecordBinding
import com.mulkkam.domain.DailyWaterIntake
import com.mulkkam.ui.binding.BindingFragment
import com.mulkkam.ui.record.adapter.RecordAdapter

class RecordFragment :
    BindingFragment<FragmentRecordBinding>(
        FragmentRecordBinding::inflate,
    ) {
    private val viewModel: RecordViewModel by viewModels()
    private val recordAdapter: RecordAdapter by lazy { RecordAdapter() }

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
                binding.pcWeeklySun,
                binding.pcWeeklyMon,
                binding.pcWeeklyTue,
                binding.pcWeeklyWed,
                binding.pcWeeklyThu,
                binding.pcWeeklyFri,
                binding.pcWeeklySat,
                binding.pcDailyWaterChart,
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
            updateWeeklyChart(weeklyWaterIntake)
        }

        viewModel.dailyWaterIntake.observe(viewLifecycleOwner) { dailyWaterIntake ->
            updateDailyWaterChart(dailyWaterIntake)
        }

        viewModel.dailyWaterRecords.observe(viewLifecycleOwner) { waterRecords ->
            recordAdapter.changeItems(waterRecords)
        }
    }

    private fun updateWeeklyChart(weeklyWaterIntake: List<DailyWaterIntake>) {
        val pieCharts =
            listOf(
                binding.pcWeeklySun,
                binding.pcWeeklyMon,
                binding.pcWeeklyTue,
                binding.pcWeeklyWed,
                binding.pcWeeklyThu,
                binding.pcWeeklyFri,
                binding.pcWeeklySat,
            )

        pieCharts.forEachIndexed { index, chart ->
            val intake =
                weeklyWaterIntake.getOrNull(index)
                    ?: DailyWaterIntake.EMPTY_DAILY_WATER_INTAKE.copy(date = weeklyWaterIntake.first().date.plusDays(index.toLong()))

            chart.setOnClickListener {
                viewModel.updateDailyWaterIntake(intake)
            }

            updateChartData(chart, intake)
        }
    }

    private fun updateDailyWaterChart(dailyWaterIntake: DailyWaterIntake) {
        val pieChart = binding.pcDailyWaterChart
        updateChartData(pieChart, dailyWaterIntake)
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

    private fun createPieData(goalRate: Float): PieData {
        val entries =
            listOf(
                PieEntry(goalRate),
                PieEntry(MAX_PERCENTAGE - goalRate),
            )

        val colors =
            listOf(
                ContextCompat.getColor(requireContext(), R.color.primary_300),
                ContextCompat.getColor(requireContext(), R.color.gray_200),
            )

        val dataSet =
            PieDataSet(entries, "").apply {
                this.colors = colors
                setDrawValues(false)
            }

        return PieData(dataSet)
    }

    companion object {
        private const val MAX_PERCENTAGE: Float = 100f
        private const val ANIMATION_DURATION_MS: Int = 1000
        private const val HOLE_RADIUS: Float = 60f
    }
}
