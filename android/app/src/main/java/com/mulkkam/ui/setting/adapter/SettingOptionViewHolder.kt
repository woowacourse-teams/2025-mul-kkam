package com.mulkkam.ui.setting.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mulkkam.databinding.ItemSettingOptionBinding
import com.mulkkam.ui.setting.model.SettingsMenu

class SettingOptionViewHolder(
    private val binding: ItemSettingOptionBinding,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(
        menu: SettingsMenu,
        handler: Handler,
    ) {
        with(binding) {
            tvSettingDescription.setText(menu.titleResId)
            root.setOnClickListener {
                handler.onClick(menu)
            }
        }
    }

    fun interface Handler {
        fun onClick(menu: SettingsMenu)
    }

    companion object {
        fun from(parent: ViewGroup): SettingOptionViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemSettingOptionBinding.inflate(inflater, parent, false)
            return SettingOptionViewHolder(binding)
        }
    }
}
