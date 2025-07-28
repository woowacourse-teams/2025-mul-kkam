package com.mulkkam.ui.home

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.annotation.ColorRes
import androidx.fragment.app.viewModels
import com.mulkkam.R
import com.mulkkam.databinding.FragmentHomeBinding
import com.mulkkam.domain.IntakeHistorySummary
import com.mulkkam.ui.binding.BindingFragment
import com.mulkkam.ui.main.Refreshable
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

        binding.fabHomeDrink.setOnClickListener {
            viewModel.addWaterIntake()
        }
    }

    private fun initObservers() {
        viewModel.todayIntakeHistorySummary.observe(viewLifecycleOwner) { summary ->
            with(binding) {
                pbHomeWaterProgress.max = summary.targetAmount
                pbHomeWaterProgress.progress = summary.totalIntakeAmount
            }
            updateDailyIntakeSummary(summary)
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
            getColoredSpannable(
                summaryColorResId,
                getString(
                    R.string.home_daily_intake_summary,
                    intakeHistorySummary.totalIntakeAmount,
                    intakeHistorySummary.targetAmount,
                ),
                formattedIntake,
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

    override fun onReselected() {
        viewModel.loadTodayIntakeHistorySummary()
        viewModel.loadCups()
    }
}
