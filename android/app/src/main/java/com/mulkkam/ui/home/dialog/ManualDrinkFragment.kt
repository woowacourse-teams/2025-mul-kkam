package com.mulkkam.ui.home.dialog

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.mulkkam.databinding.FragmentManualDrinkBinding
import com.mulkkam.domain.model.intake.IntakeType
import com.mulkkam.ui.custom.chip.MulKkamChipGroupAdapter
import com.mulkkam.ui.home.HomeViewModel
import com.mulkkam.ui.util.binding.BindingBottomSheetDialogFragment
import com.mulkkam.ui.util.extensions.setSingleClickListener
import com.mulkkam.util.extensions.setOnImeActionDoneListener

class ManualDrinkFragment :
    BindingBottomSheetDialogFragment<FragmentManualDrinkBinding>(
        FragmentManualDrinkBinding::inflate,
    ) {
    private val viewModel: HomeViewModel by activityViewModels()

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        initClickListeners()
        initChips()
        initDoneListener()
    }

    private fun initClickListeners() {
        with(binding) {
            ivClose.setSingleClickListener { dismiss() }

            tvSave.setSingleClickListener {
                viewModel.addWaterIntake(
                    binding.etAmount.text
                        .toString()
                        .toIntOrNull() ?: return@setSingleClickListener,
                )
                dismiss()
            }
        }
    }

    private fun initDoneListener() {
        binding.etAmount.setOnImeActionDoneListener((requireContext()))
    }

    private fun initChips() {
        val intakeAdapter =
            MulKkamChipGroupAdapter<IntakeType>(
                context = requireContext(),
                labelProvider = { it.toLabel() },
                colorProvider = { it.toColorHex() },
                isMultiSelect = false,
                requireSelection = true,
                onItemSelected = {
                    // TODO: 추후 액체 종류 선택 시 추가 작업 필요
                },
            )

        binding.mcgIntakeType.setAdapter(intakeAdapter)
        binding.mcgIntakeType.setItems(
            listOf(
                IntakeType.WATER,
                IntakeType.COFFEE,
            ),
        )
    }

    companion object {
        const val TAG: String = "MANUAL_DRINK_FRAGMENT"

        fun newInstance(): ManualDrinkFragment = ManualDrinkFragment()
    }
}
