package com.mulkkam.ui.settingcups

import android.content.Context
import android.content.Intent
import android.os.Bundle
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

    private val handler: SettingCupsAdapter.Handler = handleSettingCupClick()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initRecyclerView()
        initObserver()
        initClickListener()
    }

    private fun initRecyclerView() {
        binding.rvCups.adapter = settingCupsAdapter
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

            override fun onCupsOrderChanged(newOrder: List<SettingCupsItem.CupItem>) {
                viewModel.updateCupOrder(newOrder.map { cupItem -> cupItem.value })
            }
        }

    private fun initObserver() {
        viewModel.cupsUiState.observe(this) { cupsUiState ->
            handleCupsUiState(cupsUiState)
        }
    }

    private fun handleCupsUiState(cupsUiState: MulKkamUiState<CupsUiModel>) {
        when (cupsUiState) {
            is MulKkamUiState.Success<CupsUiModel> -> showCupsInfo(cupsUiState)
            is MulKkamUiState.Loading -> binding.sflCups.visibility = View.VISIBLE
            is MulKkamUiState.Idle -> Unit
            is MulKkamUiState.Failure -> {
                CustomSnackBar.make(
                    binding.root,
                    getString(R.string.load_info_error),
                    R.drawable.ic_alert_circle,
                )
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

    private fun initClickListener() {
        binding.ivBack.setSingleClickListener {
            finish()
        }
    }

    private fun showEditBottomSheetDialog(cup: CupUiModel?) {
        if (supportFragmentManager.findFragmentByTag(SettingCupFragment.TAG) != null) return
        SettingCupFragment
            .newInstance(cup)
            .show(supportFragmentManager, SettingCupFragment.TAG)
    }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, SettingCupsActivity::class.java)
    }
}
