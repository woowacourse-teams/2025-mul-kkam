package com.mulkkam.ui.settingwater.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mulkkam.databinding.ItemSettingWaterAddBinding

class AddViewHolder(
    parent: ViewGroup,
    handler: Handler,
) : SettingWaterViewHolder<SettingWaterItem.AddItem, ItemSettingWaterAddBinding>(
        ItemSettingWaterAddBinding.inflate(LayoutInflater.from(parent.context), parent, false),
    ) {
    init {
        initClickListener(handler)
    }

    private fun initClickListener(handler: Handler) {
        binding.root.setOnClickListener {
            handler.onAddClick()
        }
    }

    fun interface Handler {
        fun onAddClick()
    }
}
