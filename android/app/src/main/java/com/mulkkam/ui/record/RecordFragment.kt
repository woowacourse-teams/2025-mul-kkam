package com.mulkkam.ui.record

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.mulkkam.R
import com.mulkkam.databinding.FragmentRecordBinding
import com.mulkkam.domain.DailyWaterIntake
import com.mulkkam.domain.WaterRecord
import com.mulkkam.ui.binding.BindingFragment
import com.mulkkam.ui.main.Refreshable
import com.mulkkam.ui.record.adapter.RecordAdapter
import java.time.LocalDate
import java.time.LocalTime

class RecordFragment :
    BindingFragment<FragmentRecordBinding>(
        FragmentRecordBinding::inflate,
    ),
    Refreshable {
    private val recordAdapter: RecordAdapter by lazy {
        RecordAdapter(WATER_RECORD)
    }

    override fun onSelected() {
        // TODO: 화면 전환 시 필요한 작업을 구현합니다.
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initRecordAdapter()
        initWeeklyWaterChart(WEEKLY_WATER_INTAKE)
        updateDailyWaterChart(DAILY_WATER_INTAKE)
    }

    private fun initRecordAdapter() {
        with(binding.rvWaterRecord) {
            adapter = recordAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun initWeeklyWaterChart(weeklyWaterIntake: List<DailyWaterIntake>) {
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
            val intake = weeklyWaterIntake.getOrNull(index)
            if (intake == null) {
                chart.clear()
                return@forEachIndexed
            }

            chart.data = createPieData(intake.goalRate)
            updateChart(chart)
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

    private fun updateChart(chart: PieChart) {
        chart.apply {
            description.isEnabled = false
            legend.isEnabled = false
            setTouchEnabled(false)
            holeRadius = 60f
            animateY(1000, Easing.EaseInOutQuad)
            invalidate()
        }
    }

    private fun updateDailyWaterChart(dailyWaterIntake: DailyWaterIntake) {
        val pieChart = binding.pcDailyWaterChart
        pieChart.data = createPieData(dailyWaterIntake.goalRate)
        updateChart(pieChart)
    }

    companion object {
        private const val MAX_PERCENTAGE: Float = 100f

        val WEEKLY_WATER_INTAKE: List<DailyWaterIntake> =
            listOf(
                DailyWaterIntake(
                    1,
                    LocalDate.now(),
                    1200,
                    500,
                    10f,
                ),
                DailyWaterIntake(
                    2,
                    LocalDate.now(),
                    1200,
                    500,
                    20f,
                ),
                DailyWaterIntake(
                    3,
                    LocalDate.now(),
                    1200,
                    500,
                    30f,
                ),
                DailyWaterIntake(
                    4,
                    LocalDate.now(),
                    1200,
                    500,
                    40f,
                ),
                DailyWaterIntake(
                    5,
                    LocalDate.now(),
                    1200,
                    500,
                    50f,
                ),
                DailyWaterIntake(
                    6,
                    LocalDate.now(),
                    1200,
                    500,
                    60f,
                ),
                DailyWaterIntake(
                    7,
                    LocalDate.now(),
                    1200,
                    500,
                    70f,
                ),
            )

        val DAILY_WATER_INTAKE: DailyWaterIntake =
            DailyWaterIntake(
                1,
                LocalDate.now(),
                1200,
                500,
                50f,
            )

        val WATER_RECORD: List<WaterRecord> =
            listOf(
                WaterRecord(
                    1,
                    LocalTime.now(),
                    100,
                ),
                WaterRecord(
                    2,
                    LocalTime.now(),
                    100,
                ),
                WaterRecord(
                    3,
                    LocalTime.now(),
                    100,
                ),
                WaterRecord(
                    4,
                    LocalTime.now(),
                    100,
                ),
            )
    }
}
