package com.mulkkam.ui.settingcups

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.mulkkam.databinding.ActivitySettingCupsBinding
import com.mulkkam.ui.binding.BindingActivity
import com.mulkkam.ui.settingcups.adapter.SettingCupsAdapter
import com.mulkkam.ui.settingcups.adapter.SettingCupsItem
import com.mulkkam.ui.settingcups.dialog.SettingCupFragment
import com.mulkkam.ui.settingcups.model.CupUiModel
import com.mulkkam.ui.settingcups.model.CupsUiModel

class SettingCupsActivity : BindingActivity<ActivitySettingCupsBinding>(ActivitySettingCupsBinding::inflate) {
    private val viewModel: SettingCupsViewModel by viewModels()
    private val settingCupsAdapter: SettingCupsAdapter by lazy {
        handleSettingWaterClick()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initClickListener()
        initCupItemsContainer()
        initObserver()
        viewModel.loadCups()
    }

    private fun handleSettingWaterClick() =
        SettingCupsAdapter(
            object : SettingCupsAdapter.Handler {
                override fun onEditClick(cup: CupUiModel) {
                    showEditBottomSheetDialog(cup)
                }

                override fun onAddClick() {
                    // TODO: 컵 최대 개수 제한
                    showEditBottomSheetDialog(null)
                }
            },
        )

    private fun showEditBottomSheetDialog(cup: CupUiModel?) {
        if (supportFragmentManager.findFragmentByTag(SettingCupFragment.TAG) != null) return

        SettingCupFragment
            .newInstance(cup)
            .show(supportFragmentManager, SettingCupFragment.TAG)
    }

    private fun initClickListener() {
        binding.ivBack.setOnClickListener {
            finish()
        }
    }

    private fun initCupItemsContainer() {
        binding.rvCups.adapter = settingCupsAdapter
    }

    private fun initObserver() {
        viewModel.cups.observe(this) { cups ->
            showCups(cups)
        }
    }

    private fun showCups(cups: CupsUiModel) {
        val cupItems: List<SettingCupsItem> =
            buildList {
                addAll(cups.cups.map { SettingCupsItem.CupItem(it) })
                if (cups.isAddable) add(SettingCupsItem.AddItem)
            }
        settingCupsAdapter.submitList(cupItems)
    }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, SettingCupsActivity::class.java)
    }
}
