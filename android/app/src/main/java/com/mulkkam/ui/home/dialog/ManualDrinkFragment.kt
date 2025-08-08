package com.mulkkam.ui.home.dialog

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.mulkkam.R
import com.mulkkam.databinding.FragmentManualDrinkBinding
import com.mulkkam.domain.model.IntakeType
import com.mulkkam.ui.binding.BindingBottomSheetDialogFragment
import com.mulkkam.ui.custom.MulKkamChipGroupAdapter
import com.mulkkam.ui.home.HomeViewModel

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
        initObservers()
        initChips()
    }

    private fun initClickListeners() {
        with(binding) {
            ivClose.setOnClickListener { dismiss() }

            tvSave.setOnClickListener {
                viewModel.addWaterIntake(
                    binding.etAmount.text
                        .toString()
                        .toIntOrNull() ?: return@setOnClickListener,
                )
            }
        }
    }

    private fun initObservers() {
        viewModel.drinkSuccess.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), requireContext().getString(R.string.manual_drink_success), Toast.LENGTH_SHORT).show()
            dismiss()
        }
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
