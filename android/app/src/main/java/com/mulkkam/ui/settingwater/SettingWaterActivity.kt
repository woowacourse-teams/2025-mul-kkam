package com.mulkkam.ui.settingwater

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.mulkkam.databinding.ActivitySettingWaterBinding
import com.mulkkam.ui.binding.BindingActivity
import com.mulkkam.ui.settingwater.adapter.SettingWaterAdapter
import com.mulkkam.ui.settingwater.adapter.SettingWaterItem
import com.mulkkam.ui.settingwater.dialog.SettingWaterCupFragment
import com.mulkkam.ui.settingwater.model.CupUiModel
import com.mulkkam.ui.settingwater.model.CupsUiModel

class SettingWaterActivity : BindingActivity<ActivitySettingWaterBinding>(ActivitySettingWaterBinding::inflate) {
    private val viewModel: SettingWaterViewModel by viewModels()
    private val settingWaterAdapter: SettingWaterAdapter by lazy {
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
        SettingWaterAdapter(
            object : SettingWaterAdapter.Handler {
                override fun onEditClick(cup: CupUiModel) {
                    showEditBottomSheetDialog(cup)
                }

                override fun onDeleteClick(id: Int) {
                    // TODO: 삭제 API 호출
                }

                override fun onAddClick() {
                    // TODO: 컵 최대 개수 제한
                    showEditBottomSheetDialog(null)
                }
            },
        )

    private fun showEditBottomSheetDialog(cup: CupUiModel?) {
        if (supportFragmentManager.findFragmentByTag(SettingWaterCupFragment.TAG) != null) return

        SettingWaterCupFragment
            .newInstance(cup)
            .show(supportFragmentManager, SettingWaterCupFragment.TAG)
    }

    private fun initClickListener() {
        binding.ivBack.setOnClickListener {
            finish()
        }
    }

    private fun initCupItemsContainer() {
        binding.rvSettingWater.adapter = settingWaterAdapter
    }

    private fun initObserver() {
        viewModel.cups.observe(this) { cups ->
            showCups(cups)
        }
    }

    private fun showCups(cups: CupsUiModel) {
        val cupItems: List<SettingWaterItem> =
            buildList {
                addAll(cups.cups.map { SettingWaterItem.CupItem(it) })
                if (cups.isAddable) add(SettingWaterItem.AddItem)
            }
        settingWaterAdapter.submitList(cupItems)
    }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, SettingWaterActivity::class.java)
    }
}
