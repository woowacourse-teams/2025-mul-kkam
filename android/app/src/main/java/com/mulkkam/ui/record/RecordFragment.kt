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
import com.mulkkam.domain.WaterRecords
import com.mulkkam.ui.binding.BindingFragment
import com.mulkkam.ui.record.adapter.RecordAdapter
import java.time.LocalDate
import java.time.LocalTime

class RecordFragment :
    BindingFragment<FragmentRecordBinding>(
        FragmentRecordBinding::inflate,
    ) {
    private val recordAdapter: RecordAdapter by lazy {
        RecordAdapter(WATER_RECORD[1].waterRecords)
    }
    private val viewModel by lazy { RecordViewModel() }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initRecordAdapter()
        initWeeklyWaterChart(WEEKLY_WATER_INTAKE)
        initDailyWaterChart(WEEKLY_WATER_INTAKE.get(index = 1))
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

            updateWeeklyChart(chart, intake)
            initChartOptions(chart)
        }
    }

    private fun updateWeeklyChart(
        chart: PieChart,
        intake: DailyWaterIntake,
    ) {
        chart.setOnClickListener {
            updateDailyWaterChart(intake)
            recordAdapter.changeItems(WATER_RECORD.find { it.date == intake.date }?.waterRecords ?: listOf())
        }

        chart.data = createPieData(intake.goalRate)
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

    private fun initChartOptions(chart: PieChart) {
        chart.apply {
            description.isEnabled = false
            legend.isEnabled = false
            setTouchEnabled(false)
            holeRadius = 60f
            animateY(1000, Easing.EaseInOutQuad)
            invalidate()
        }
    }

    private fun initDailyWaterChart(dailyWaterIntake: DailyWaterIntake) {
        val pieChart = binding.pcDailyWaterChart
        initChartOptions(pieChart)
        updateDailyWaterChart(dailyWaterIntake)
    }

    private fun updateDailyWaterChart(dailyWaterIntake: DailyWaterIntake) {
        val pieChart = binding.pcDailyWaterChart
        pieChart.data = createPieData(dailyWaterIntake.goalRate)
    }

    companion object {
        private const val MAX_PERCENTAGE: Float = 100f

        val WEEKLY_WATER_INTAKE: List<DailyWaterIntake> =
            listOf(
                DailyWaterIntake(
                    1,
                    LocalDate.of(2025, 7, 21),
                    1200,
                    500,
                    10f,
                ),
                DailyWaterIntake(
                    2,
                    LocalDate.of(2025, 7, 22),
                    1200,
                    500,
                    20f,
                ),
                DailyWaterIntake(
                    3,
                    LocalDate.of(2025, 7, 23),
                    1200,
                    500,
                    30f,
                ),
                DailyWaterIntake(
                    4,
                    LocalDate.of(2025, 7, 24),
                    1200,
                    500,
                    40f,
                ),
                DailyWaterIntake(
                    5,
                    LocalDate.of(2025, 7, 25),
                    1200,
                    500,
                    50f,
                ),
                DailyWaterIntake(
                    6,
                    LocalDate.of(2025, 7, 26),
                    1200,
                    500,
                    60f,
                ),
                DailyWaterIntake(
                    7,
                    LocalDate.of(2025, 7, 27),
                    1200,
                    500,
                    70f,
                ),
            )

        val WATER_RECORD: List<WaterRecords> =
            listOf(
                WaterRecords(
                    LocalDate.of(2025, 7, 21),
                    listOf(
                        WaterRecord(
                            1,
                            LocalTime.now(),
                            100,
                        ),
                        WaterRecord(
                            1,
                            LocalTime.now(),
                            200,
                        ),
                    ),
                ),
                WaterRecords(
                    LocalDate.of(2025, 7, 22),
                    listOf(
                        WaterRecord(
                            2,
                            LocalTime.now(),
                            100,
                        ),
                        WaterRecord(
                            2,
                            LocalTime.now(),
                            100,
                        ),
                        WaterRecord(
                            2,
                            LocalTime.now(),
                            100,
                        ),
                    ),
                ),
                WaterRecords(
                    LocalDate.of(2025, 7, 23),
                    listOf(
                        WaterRecord(
                            3,
                            LocalTime.now(),
                            300,
                        ),
                    ),
                ),
                WaterRecords(
                    LocalDate.of(2025, 7, 24),
                    listOf(
                        WaterRecord(
                            4,
                            LocalTime.now(),
                            400,
                        ),
                        WaterRecord(
                            4,
                            LocalTime.now(),
                            400,
                        ),
                    ),
                ),
            )
    }
}
