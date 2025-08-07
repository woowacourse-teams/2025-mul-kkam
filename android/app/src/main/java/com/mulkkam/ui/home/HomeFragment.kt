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
import com.mulkkam.domain.IntakeHistorySummary
import com.mulkkam.domain.model.Cups
import com.mulkkam.ui.binding.BindingFragment
import com.mulkkam.ui.custom.ExtendableFloatingMenuIcon
import com.mulkkam.ui.custom.ExtendableFloatingMenuItem
import com.mulkkam.ui.home.dialog.ManualDrinkFragment
import com.mulkkam.ui.main.Refreshable
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
    }

    private fun initObservers() {
        viewModel.todayIntakeHistorySummary.observe(viewLifecycleOwner) { summary ->
            binding.pbHomeWaterProgress.setProgress(summary.achievementRate)
            updateDailyIntakeSummary(summary)
        }
        viewModel.cups.observe(viewLifecycleOwner) { cups ->
            updateDrinkMenu(cups)
        }
        viewModel.characterChat.observe(viewLifecycleOwner) { chat ->
            binding.tvHomeCharacterChat.text = chat ?: return@observe
        }
    }

    private fun updateDrinkMenu(cups: Cups) {
        binding.fabHomeDrink.setMenuItems(
            items =
                cups.cups.map { cup ->
                    ExtendableFloatingMenuItem(
                        label = cup.nickname,
                        icon = ExtendableFloatingMenuIcon.Url(cup.emoji),
                        data = cup,
                    )
                } +
                    ExtendableFloatingMenuItem(
                        label = getString(R.string.home_drink_manual),
                        icon = ExtendableFloatingMenuIcon.Resource(R.drawable.ic_manual_drink),
                        data = null,
                    ),
            onItemClick = {
                if (it.data == null) {
                    showManualDrinkBottomSheetDialog()
                } else {
                    viewModel.addWaterIntakeByCup(it.data.id)
                }
            },
        )
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

    private fun showManualDrinkBottomSheetDialog() {
        if (childFragmentManager.findFragmentByTag(ManualDrinkFragment.TAG) != null) return
        ManualDrinkFragment
            .newInstance()
            .show(childFragmentManager, ManualDrinkFragment.TAG)
    }

    override fun onReselected() {
        viewModel.loadTodayIntakeHistorySummary()
        viewModel.loadCups()
        binding.fabHomeDrink.closeMenu()
    }

    companion object {
        private const val PROGRESS_BAR_RADIUS: Float = 12f
    }
}
