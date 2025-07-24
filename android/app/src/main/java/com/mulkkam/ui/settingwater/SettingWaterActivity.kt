package com.mulkkam.ui.settingwater

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.mulkkam.databinding.ActivitySettingWaterBinding
import com.mulkkam.ui.binding.BindingActivity
import com.mulkkam.ui.settingwater.adapter.SettingWaterAdapter
import com.mulkkam.ui.settingwater.adapter.SettingWaterItem
import com.mulkkam.ui.settingwater.dialog.SettingWaterCupFragment
import com.mulkkam.ui.settingwater.model.CupUiModel

class SettingWaterActivity : BindingActivity<ActivitySettingWaterBinding>(ActivitySettingWaterBinding::inflate) {
    private val settingWaterAdapter: SettingWaterAdapter by lazy {
        handleSettingWaterClick()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initClickListener()
        initCupItemsContainer()
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
        settingWaterAdapter.submitList(DUMMY_CUP_ITEMS)
    }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, SettingWaterActivity::class.java)

        private val DUMMY_CUP_ITEMS =
            listOf(
                SettingWaterItem.CupItem(
                    value =
                        CupUiModel(
                            id = 8965,
                            nickname = "스타벅스 텀블러",
                            amount = 355,
                            rank = 1,
                            isRepresentative = true,
                        ),
                ),
                SettingWaterItem.CupItem(
                    value =
                        CupUiModel(
                            id = 1787,
                            nickname = "우리집 컵",
                            amount = 120,
                            rank = 2,
                        ),
                ),
                SettingWaterItem.CupItem(
                    value =
                        CupUiModel(
                            id = 1234,
                            nickname = "500ML 생맥주",
                            amount = 500,
                            rank = 3,
                        ),
                ),
                SettingWaterItem.AddItem,
            )
    }
}
