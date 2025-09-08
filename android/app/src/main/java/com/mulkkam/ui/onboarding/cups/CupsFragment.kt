package com.mulkkam.ui.onboarding.cups

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.mulkkam.R
import com.mulkkam.databinding.FragmentCupsBinding
import com.mulkkam.ui.custom.snackbar.CustomSnackBar
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.model.MulKkamUiState.Idle.toSuccessDataOrNull
import com.mulkkam.ui.onboarding.OnboardingViewModel
import com.mulkkam.ui.onboarding.cups.dialog.CupBottomSheetFragment
import com.mulkkam.ui.onboarding.cups.dialog.CupBottomSheetFragment.Companion.BUNDLE_KEY_CUP
import com.mulkkam.ui.onboarding.cups.dialog.CupBottomSheetFragment.Companion.REQUEST_KEY_CUP
import com.mulkkam.ui.settingcups.adapter.CupsItemTouchHelperCallback
import com.mulkkam.ui.settingcups.adapter.SettingCupsAdapter
import com.mulkkam.ui.settingcups.adapter.SettingCupsItem
import com.mulkkam.ui.settingcups.dialog.SettingCupFragment
import com.mulkkam.ui.settingcups.model.CupUiModel
import com.mulkkam.ui.settingcups.model.CupsUiModel
import com.mulkkam.ui.util.binding.BindingFragment
import com.mulkkam.ui.util.extensions.getAppearanceSpannable
import com.mulkkam.ui.util.extensions.setSingleClickListener

class CupsFragment :
    BindingFragment<FragmentCupsBinding>(
        FragmentCupsBinding::inflate,
    ) {
    private val parentViewModel: OnboardingViewModel by activityViewModels()
    private val viewModel: CupsViewModel by viewModels()
    private val settingCupsAdapter: SettingCupsAdapter by lazy {
        SettingCupsAdapter(handler)
    }
    private val itemTouchHelper: ItemTouchHelper by lazy {
        ItemTouchHelper(CupsItemTouchHelperCallback(settingCupsAdapter))
    }

    private val handler: SettingCupsAdapter.Handler = handleSettingCupClick()

    private fun handleSettingCupClick() =
        object : SettingCupsAdapter.Handler {
            override fun onEditClick(cup: CupUiModel) {
                showEditBottomSheetDialog(cup)
            }

            override fun onOrderDrag(viewHolder: RecyclerView.ViewHolder) {
                itemTouchHelper.startDrag(viewHolder)
            }

            override fun onAddClick() {
                showEditBottomSheetDialog(null)
            }

            override fun onDropAttempt(newOrder: List<SettingCupsItem.CupItem>) {
                viewModel.updateCupOrder(newOrder.map { cupItem -> cupItem.value })
            }
        }

    private fun showEditBottomSheetDialog(cup: CupUiModel?) {
        if (parentFragmentManager.findFragmentByTag(SettingCupFragment.TAG) != null) return
        CupBottomSheetFragment
            .newInstance(cup)
            .show(parentFragmentManager, SettingCupFragment.TAG)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        initTextAppearance()
        initAdapter()
        initObserver()
        initClickListener()
        parentFragmentManager.setFragmentResultListener(
            REQUEST_KEY_CUP,
            viewLifecycleOwner,
        ) { _, bundle ->
            val updatedCup = bundle.getParcelable<CupUiModel>(BUNDLE_KEY_CUP)
            viewModel.updateCup(updatedCup)
        }
    }

    private fun initTextAppearance() {
        binding.tvViewLabel.text =
            getString(R.string.cups_input_hint, parentViewModel.onboardingInfo.nickname?.name).getAppearanceSpannable(
                requireContext(),
                R.style.title1,
                getString(R.string.cups_input_hint_highlight),
            )
    }

    private fun initAdapter() {
        binding.rvCups.adapter = settingCupsAdapter
    }

    private fun initObserver() {
        with(viewModel) {
            cupsUiState.observe(viewLifecycleOwner) { cupsUiState ->
                handleCupsUiState(cupsUiState)
            }

            cupsResetUiState.observe(viewLifecycleOwner) { cupsResetUiState ->
                handleCupsResetUiState(cupsResetUiState)
            }
        }
    }

    private fun handleCupsUiState(cupsUiState: MulKkamUiState<CupsUiModel>) {
        when (cupsUiState) {
            is MulKkamUiState.Success<CupsUiModel> -> showCupsInfo(cupsUiState)
            is MulKkamUiState.Loading -> binding.sflCups.visibility = View.VISIBLE
            is MulKkamUiState.Idle -> Unit
            is MulKkamUiState.Failure -> {
                CustomSnackBar
                    .make(
                        binding.root,
                        getString(R.string.load_info_error),
                        R.drawable.ic_alert_circle,
                    ).show()
                binding.sflCups.visibility = View.GONE
            }
        }
    }

    private fun showCupsInfo(cupsUiState: MulKkamUiState.Success<CupsUiModel>) {
        val cupItems =
            buildList {
                addAll(cupsUiState.data.cups.map { SettingCupsItem.CupItem(it) })
                if (cupsUiState.data.isAddable) add(SettingCupsItem.AddItem)
            }
        settingCupsAdapter.submitList(cupItems)
        binding.sflCups.visibility = View.GONE
    }

    private fun handleCupsResetUiState(cupsResetUiState: MulKkamUiState<Unit>) {
        when (cupsResetUiState) {
            is MulKkamUiState.Success<Unit> -> {
                CustomSnackBar
                    .make(
                        binding.root,
                        getString(R.string.setting_cups_reset_success),
                        R.drawable.ic_terms_all_check_on,
                    ).show()
            }

            is MulKkamUiState.Idle -> Unit
            is MulKkamUiState.Loading -> Unit
            is MulKkamUiState.Failure ->
                CustomSnackBar
                    .make(
                        binding.root,
                        getString(R.string.network_check_error),
                        R.drawable.ic_alert_circle,
                    ).show()
        }
    }

    private fun initClickListener() {
        binding.tvComplete.setSingleClickListener {
            parentViewModel.updateCups(
                viewModel.cupsUiState.value
                    ?.toSuccessDataOrNull()
                    ?.cups ?: emptyList(),
            )
            parentViewModel.completeOnboarding()
        }
    }
}
