package com.mulkkam.ui.history

import android.content.res.ColorStateList
import android.graphics.SweepGradient
import android.os.Bundle
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
import com.mulkkam.databinding.LayoutHistoryWaterIntakeChartBinding
import com.mulkkam.domain.model.intake.IntakeHistory
import com.mulkkam.domain.model.intake.IntakeHistorySummaries
import com.mulkkam.domain.model.intake.IntakeHistorySummary
import com.mulkkam.domain.model.intake.WaterIntakeState
import com.mulkkam.ui.custom.snackbar.CustomSnackBar
import com.mulkkam.ui.history.adapter.HistoryAdapter
import com.mulkkam.ui.history.adapter.HistoryViewHolder
import com.mulkkam.ui.history.dialog.DeleteConfirmDialogFragment
import com.mulkkam.ui.main.Refreshable
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.util.binding.BindingFragment
import com.mulkkam.ui.util.extensions.getColoredSpannable
import com.mulkkam.ui.util.extensions.setSingleClickListener
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
    private var selectedChartBinding: LayoutHistoryWaterIntakeChartBinding? = null
    private val weeklyCharts: List<LayoutHistoryWaterIntakeChartBinding> by lazy {
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
    private var historyToDelete: IntakeHistory? = null

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
        initDialogResultListener()
    }

    private fun initHistoryAdapter() {
        with(binding.rvIntakeHistory) {
            adapter = historyAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        historyAdapter.onItemLongClickListener =
            HistoryViewHolder.Handler { history ->
                this.historyToDelete = history
                DeleteConfirmDialogFragment().show(
                    childFragmentManager,
                    DeleteConfirmDialogFragment.TAG,
                )
            }
    }

    private fun initDialogResultListener() {
        childFragmentManager.setFragmentResultListener(
            DeleteConfirmDialogFragment.REQUEST_KEY,
            viewLifecycleOwner,
        ) { requestKey, bundle ->
            val isConfirmed = bundle.getBoolean(DeleteConfirmDialogFragment.BUNDLE_KEY_CONFIRM)
            if (isConfirmed) {
                historyToDelete?.let {
                    viewModel.deleteIntakeHistory(it)
                }
                historyToDelete = null
            }
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
        viewModel.weeklyIntakeHistoriesUiState.observe(viewLifecycleOwner) { weeklyIntakeHistoriesUiState ->
            handleWeeklyIntakeHistoriesUiState(weeklyIntakeHistoriesUiState ?: return@observe)
        }

        viewModel.dailyIntakeHistories.observe(viewLifecycleOwner) { dailyIntakeHistories ->
            updateDailyChart(dailyIntakeHistories)
            updateIntakeHistories(dailyIntakeHistories.intakeHistories)
            updateWeeklyChartHighlight(dailyIntakeHistories.dayOfWeekIndex())
        }

        viewModel.isNotCurrentWeek.observe(viewLifecycleOwner) { canMoveToNext ->
            binding.ibWeekNext.isVisible = canMoveToNext
        }

        viewModel.waterIntakeState.observe(viewLifecycleOwner) { waterIntakeState ->
            binding.tvTodayLabel.isVisible = waterIntakeState is WaterIntakeState.Present
            updateCharacterImage(waterIntakeState)
        }

        viewModel.deleteSuccess.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) {
                CustomSnackBar
                    .make(
                        binding.root,
                        getString(R.string.history_delete_success),
                        R.drawable.ic_terms_all_check_on,
                    ).show()
            }
        }
    }

    private fun handleWeeklyIntakeHistoriesUiState(weeklyIntakeHistoriesUiState: MulKkamUiState<IntakeHistorySummaries>) {
        when (weeklyIntakeHistoriesUiState) {
            is MulKkamUiState.Success<IntakeHistorySummaries> -> bindWeeklyChartData(weeklyIntakeHistoriesUiState.data)
            MulKkamUiState.Empty -> TODO()
            MulKkamUiState.Loading -> TODO()
            is MulKkamUiState.Failure -> TODO()
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
        chart: LayoutHistoryWaterIntakeChartBinding,
        intakeHistorySummary: IntakeHistorySummary,
    ) {
        chart.apply {
            updateGoalRate(chart, intakeHistorySummary)
            root.setSingleClickListener {
                viewModel.updateDailyIntakeHistories(intakeHistorySummary, LocalDate.now())
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

    private fun updateGoalRate(
        chart: LayoutHistoryWaterIntakeChartBinding,
        intakeHistorySummary: IntakeHistorySummary,
    ) {
        with(chart) {
            if (intakeHistorySummary.achievementRate == ACHIEVEMENT_RATE_FULL) {
                tvWaterGoalRate.visibility = View.GONE
                ivCheck.visibility = View.VISIBLE
            } else {
                tvWaterGoalRate.visibility = View.VISIBLE
                ivCheck.visibility = View.GONE
                tvWaterGoalRate.text = intakeHistorySummary.achievementRate.toInt().toString()
            }
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
        updateDailyChartView(intakeHistorySummary)
        updateDailyChartLabel(intakeHistorySummary.date)
        updateDailyIntakeSummary(intakeHistorySummary)
    }

    private fun updateDailyChartView(intakeHistorySummary: IntakeHistorySummary) {
        with(binding) {
            viewDailyChart.setProgress(intakeHistorySummary.achievementRate)
        }
    }

    private fun updateDailyChartLabel(date: LocalDate) {
        binding.tvDailyChartLabel.text =
            getString(
                R.string.history_daily_chart_label,
                date.format(FORMATTER_DATE_WITH_DAY),
            ).getColoredSpannable(
                requireContext(),
                R.color.primary_200,
                date.format(FORMATTER_DATE_WITH_DAY),
            )
    }

    private fun updateDailyIntakeSummary(intakeHistorySummary: IntakeHistorySummary) {
        val formattedIntake =
            String.format(Locale.US, "%,dml", intakeHistorySummary.totalIntakeAmount)

        @ColorRes val summaryColorResId =
            if (intakeHistorySummary.targetAmount > intakeHistorySummary.totalIntakeAmount ||
                intakeHistorySummary.totalIntakeAmount == INTAKE_AMOUNT_EMPTY
            ) {
                R.color.gray_200
            } else {
                R.color.primary_200
            }
        binding.tvDailyIntakeSummary.text =
            getString(
                R.string.history_daily_intake_summary,
                intakeHistorySummary.totalIntakeAmount,
                intakeHistorySummary.targetAmount,
            ).getColoredSpannable(
                requireContext(),
                summaryColorResId,
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

    private fun resetChartHighlight(chart: LayoutHistoryWaterIntakeChartBinding) {
        chart.root.background = getDrawable(requireContext(), R.drawable.bg_common_rectangle_4dp)
        ViewCompat.setBackgroundTintList(
            chart.root,
            ColorStateList.valueOf(getColor(requireContext(), R.color.gray_10)),
        )
    }

    private fun applyChartHighlight(chart: LayoutHistoryWaterIntakeChartBinding) {
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

    private fun updateCharacterImage(waterIntakeState: WaterIntakeState) {
        val characterImage =
            when (waterIntakeState) {
                is WaterIntakeState.Past.NoRecord -> R.drawable.img_history_crying_character
                is WaterIntakeState.Past.Full -> R.drawable.img_history_character
                is WaterIntakeState.Present.Full -> R.drawable.img_history_character
                else -> R.drawable.img_history_character
            }
        binding.ivHistoryCharacter.setImageDrawable(
            getDrawable(
                requireContext(),
                characterImage,
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

        private const val INTAKE_AMOUNT_EMPTY: Int = 0
        private const val ACHIEVEMENT_RATE_FULL: Float = 100f
    }
}
