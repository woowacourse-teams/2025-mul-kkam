package com.mulkkam.ui.settingcups.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mulkkam.databinding.ItemSettingCupsAddBinding
import com.mulkkam.ui.util.extensions.setSingleClickListener

class AddViewHolder(
    parent: ViewGroup,
    handler: Handler,
) : SettingCupsViewHolder<SettingCupsItem.AddItem, ItemSettingCupsAddBinding>(
        ItemSettingCupsAddBinding.inflate(LayoutInflater.from(parent.context), parent, false),
    ) {
    init {
        initClickListener(handler)
    }

    private fun initClickListener(handler: Handler) {
        binding.root.setSingleClickListener {
            handler.onAddClick()
        }
    }

    fun interface Handler {
        fun onAddClick()
    }
}
