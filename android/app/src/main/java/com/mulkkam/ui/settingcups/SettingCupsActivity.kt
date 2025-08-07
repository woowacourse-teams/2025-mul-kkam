package com.mulkkam.ui.settingcups

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.mulkkam.databinding.ActivitySettingCupsBinding
import com.mulkkam.ui.binding.BindingActivity
import com.mulkkam.ui.settingcups.adapter.CupsItemTouchHelperCallback
import com.mulkkam.ui.settingcups.adapter.SettingCupsAdapter
import com.mulkkam.ui.settingcups.adapter.SettingCupsItem
import com.mulkkam.ui.settingcups.dialog.SettingCupFragment
import com.mulkkam.ui.settingcups.model.CupUiModel

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
        viewModel.cups.observe(this) { cups ->
            val cupItems =
                buildList {
                    addAll(cups.cups.map { SettingCupsItem.CupItem(it) })
                    if (cups.isAddable) add(SettingCupsItem.AddItem)
                }
            settingCupsAdapter.submitList(cupItems)
        }
    }

    private fun initClickListener() {
        binding.ivBack.setOnClickListener {
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
