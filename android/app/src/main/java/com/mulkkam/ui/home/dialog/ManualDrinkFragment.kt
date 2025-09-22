package com.mulkkam.ui.home.dialog

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import com.mulkkam.R
import com.mulkkam.databinding.FragmentManualDrinkBinding
import com.mulkkam.domain.model.cups.CupAmount
import com.mulkkam.domain.model.intake.IntakeType
import com.mulkkam.domain.model.result.MulKkamError
import com.mulkkam.ui.custom.chip.MulKkamChipGroupAdapter
import com.mulkkam.ui.encyclopedia.CoffeeEncyclopediaActivity
import com.mulkkam.ui.home.HomeViewModel
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.util.binding.BindingBottomSheetDialogFragment
import com.mulkkam.ui.util.extensions.sanitizeLeadingZeros
import com.mulkkam.ui.util.extensions.setOnImeActionDoneListener
import com.mulkkam.ui.util.extensions.setSingleClickListener

class ManualDrinkFragment :
    BindingBottomSheetDialogFragment<FragmentManualDrinkBinding>(
        FragmentManualDrinkBinding::inflate,
    ) {
    private val parentViewModel: HomeViewModel by activityViewModels()
    private val viewModel: ManualDrinkViewModel by activityViewModels()

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        initClickListeners()
        initChips()
        initObservers()
        initInputListeners()
        initDoneListener()
    }

    private fun initClickListeners() =
        with(binding) {
            ivClose.setSingleClickListener { dismiss() }
            tvSave.setSingleClickListener {
                parentViewModel.addWaterIntake(
                    viewModel.intakeType.value ?: IntakeType.WATER,
                    binding.etAmount.text
                        .toString()
                        .toIntOrNull() ?: return@setSingleClickListener,
                )
                dismiss()
            }
            ivIntakeTypeInfo.setOnClickListener {
                val intent = CoffeeEncyclopediaActivity.newIntent(requireContext())
                startActivity(intent)
            }
        }

    private fun initObservers() =
        with(viewModel) {
            amountValidity.observe(viewLifecycleOwner) { state ->
                applyFieldColor(state)
                showAmountValidationMessage(state)
            }
            isSaveAvailable.observe(viewLifecycleOwner) { available ->
                binding.tvSave.isEnabled = available == true
            }
        }

    private fun initInputListeners() {
        binding.etAmount.doAfterTextChanged { editable ->
            val original = editable.toString()
            val processed = original.sanitizeLeadingZeros()

            if (processed != original) {
                updateEditText(binding.etAmount, processed)
                val amount = processed.toIntOrNull() ?: 0
                viewModel.updateAmount(amount)
                return@doAfterTextChanged
            }

            val amount = processed.toIntOrNull() ?: 0
            viewModel.updateAmount(amount)
        }
    }

    private fun updateEditText(
        editText: EditText,
        newText: String,
    ) {
        editText.setText(newText)
        editText.setSelection(newText.length)
    }

    private fun initDoneListener() {
        binding.etAmount.setOnImeActionDoneListener()
    }

    private fun initChips() {
        val intakeAdapter =
            MulKkamChipGroupAdapter<IntakeType>(
                context = requireContext(),
                labelProvider = { it.toLabel() },
                colorProvider = { it.toColorHex() },
                isMultiSelect = false,
                requireSelection = true,
                onItemSelected = { selected ->
                    viewModel.updateIntakeType(selected.firstOrNull() ?: IntakeType.WATER)
                },
            )

        binding.mcgIntakeType.setAdapter(intakeAdapter)
        binding.mcgIntakeType.setItems(listOf(IntakeType.WATER, IntakeType.COFFEE))
    }

    private fun applyFieldColor(state: MulKkamUiState<Unit>) {
        val colorRes =
            when (state) {
                is MulKkamUiState.Success -> R.color.primary_200
                is MulKkamUiState.Failure -> R.color.secondary_200
                else -> R.color.gray_400
            }
        val color = ContextCompat.getColor(requireContext(), colorRes)
        binding.etAmount.foregroundTintList = ColorStateList.valueOf(color)
    }

    private fun showAmountValidationMessage(state: MulKkamUiState<Unit>) {
        when (state) {
            is MulKkamUiState.Success,
            is MulKkamUiState.Idle,
            -> binding.tvAmountValidationMessage.updateMessage(null)

            is MulKkamUiState.Failure -> {
                val message =
                    if (state.error is MulKkamError.SettingCupsError.InvalidAmount) {
                        getString(R.string.home_manual_drink_invalid_range, CupAmount.MIN_ML, CupAmount.MAX_ML)
                    } else {
                        ""
                    }
                binding.tvAmountValidationMessage.updateMessage(message)
            }

            is MulKkamUiState.Loading -> Unit
        }
    }

    private fun TextView.updateMessage(message: String?) {
        text = message.orEmpty()
        visibility = if (message.isNullOrBlank()) View.GONE else View.VISIBLE
    }

    companion object {
        const val TAG: String = "MANUAL_DRINK_FRAGMENT"

        fun newInstance(): ManualDrinkFragment = ManualDrinkFragment()
    }
}
