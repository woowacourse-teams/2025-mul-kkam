package com.mulkkam.ui.settingcups

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.mulkkam.R
import com.mulkkam.databinding.ActivitySettingCupsBinding
import com.mulkkam.ui.custom.snackbar.CustomSnackBar
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.settingcups.adapter.CupsItemTouchHelperCallback
import com.mulkkam.ui.settingcups.adapter.SettingCupsAdapter
import com.mulkkam.ui.settingcups.adapter.SettingCupsItem
import com.mulkkam.ui.settingcups.dialog.SettingCupFragment
import com.mulkkam.ui.settingcups.dialog.SettingCupsResetDialogFragment
import com.mulkkam.ui.settingcups.model.CupUiModel
import com.mulkkam.ui.settingcups.model.CupsUiModel
import com.mulkkam.ui.util.binding.BindingActivity
import com.mulkkam.ui.util.extensions.setSingleClickListener

class SettingCupsActivity : BindingActivity<ActivitySettingCupsBinding>(ActivitySettingCupsBinding::inflate) {
    private val viewModel: SettingCupsViewModel by viewModels()
    private val settingCupsAdapter: SettingCupsAdapter by lazy {
        SettingCupsAdapter(handler)
    }
    private val itemTouchHelper: ItemTouchHelper by lazy {
        ItemTouchHelper(CupsItemTouchHelperCallback(settingCupsAdapter))
    }

    private val debounceHandler = Handler(Looper.getMainLooper())
    private var debounceRunnable: Runnable? = null

    private val handler: SettingCupsAdapter.Handler = handleSettingCupClick()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initRecyclerView()
        initObserver()
        initClickListener()
    }

    private fun initRecyclerView() {
        binding.rvCups.adapter = settingCupsAdapter
        binding.rvCups.itemAnimator = null
        itemTouchHelper.attachToRecyclerView(binding.rvCups)
    }

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

            override fun onDropAttempt(newCupItems: List<SettingCupsItem.CupItem>) {
                reorderCups(newCupItems)
            }
        }

    private fun showEditBottomSheetDialog(cup: CupUiModel?) {
        if (supportFragmentManager.findFragmentByTag(SettingCupFragment.TAG) != null) return
        SettingCupFragment
            .newInstance(cup)
            .show(supportFragmentManager, SettingCupFragment.TAG)
    }

    private fun reorderCups(newCupItems: List<SettingCupsItem.CupItem>) {
        val cups = newCupItems.map { it.value }
        viewModel.applyOptimisticCupOrder(cups)

        debounceRunnable?.let(debounceHandler::removeCallbacks)

        debounceRunnable =
            Runnable {
                val latest = newCupItems.map { it.value }
                viewModel.updateCupOrder(latest)
            }.also { runnable ->
                debounceHandler.postDelayed(runnable, REORDER_RANK_DELAY)
            }
    }

    private fun initObserver() {
        with(viewModel) {
            cupsUiState.observe(this@SettingCupsActivity) { cupsUiState ->
                handleCupsUiState(cupsUiState)
            }

            cupsResetUiState.observe(this@SettingCupsActivity) { cupsResetUiState ->
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
        with(binding) {
            ivBack.setSingleClickListener {
                finish()
            }
            tvReset.setSingleClickListener {
                showResetDialog()
            }
        }
    }

    private fun showResetDialog() {
        if (supportFragmentManager.findFragmentByTag(SettingCupsResetDialogFragment.TAG) != null) return
        SettingCupsResetDialogFragment
            .newInstance()
            .show(supportFragmentManager, SettingCupFragment.TAG)
    }

    override fun onDestroy() {
        super.onDestroy()
        debounceRunnable?.let(debounceHandler::removeCallbacks)
        debounceRunnable = null
    }

    companion object {
        private const val REORDER_RANK_DELAY: Long = 2000L

        fun newIntent(context: Context): Intent = Intent(context, SettingCupsActivity::class.java)
    }
}
