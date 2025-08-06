package com.mulkkam.ui.settingcups

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.mulkkam.databinding.ActivitySettingCupsBinding
import com.mulkkam.ui.binding.BindingActivity
import com.mulkkam.ui.settingcups.adapter.CupItemTouchHelperCallback
import com.mulkkam.ui.settingcups.adapter.SettingCupsAdapter
import com.mulkkam.ui.settingcups.adapter.SettingCupsItem
import com.mulkkam.ui.settingcups.dialog.SettingCupFragment
import com.mulkkam.ui.settingcups.model.CupUiModel

class SettingCupsActivity : BindingActivity<ActivitySettingCupsBinding>(ActivitySettingCupsBinding::inflate) {
    private lateinit var itemTouchHelper: ItemTouchHelper
    private val viewModel: SettingCupsViewModel by viewModels()

    private val settingCupsAdapter: SettingCupsAdapter by lazy {
        SettingCupsAdapter(handler)
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

            override fun onCupsOrderChanged(newOrder: List<SettingCupsItem.CupItem>) {
                viewModel.updateCupOrder(newOrder.map { cupItem -> cupItem.value })
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.rvCups.adapter = settingCupsAdapter

        val callback = CupItemTouchHelperCallback(settingCupsAdapter)
        itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(binding.rvCups)

        initObserver()
        initClickListener()
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
