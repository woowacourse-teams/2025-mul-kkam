package com.mulkkam.ui.settingcups.adapter

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import com.mulkkam.R
import com.mulkkam.databinding.ItemSettingCupsCupBinding
import com.mulkkam.ui.settingcups.model.CupUiModel

class CupViewHolder(
    parent: ViewGroup,
    private val handler: Handler,
    private val dragStartListener: OnStartDragListener,
) : SettingCupsViewHolder<SettingCupsItem.CupItem, ItemSettingCupsCupBinding>(
        ItemSettingCupsCupBinding.inflate(LayoutInflater.from(parent.context), parent, false),
    ) {
    override fun bind(item: SettingCupsItem.CupItem) {
        super.bind(item)
        showCupInfo(item)
        initClickListeners(item)
        initMoveListener()
    }

    private fun showCupInfo(item: SettingCupsItem.CupItem) =
        with(binding) {
            tvNickname.text = item.value.nickname
            tvIncrement.text =
                root.context.getString(
                    R.string.setting_cups_increment,
                    item.value.amount,
                )
        }

    private fun initClickListeners(item: SettingCupsItem.CupItem) =
        binding.ivEdit.setOnClickListener {
            handler.onEditClick(item.value)
        }

    private fun initMoveListener() {
        binding.ivMove.apply {
            setOnTouchListener { view, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    dragStartListener.onStartDrag(this@CupViewHolder)
                    view.performClick()
                    return@setOnTouchListener true
                }
                false
            }
            setOnClickListener { }
        }
    }

    interface Handler {
        fun onEditClick(cup: CupUiModel)
    }
}
