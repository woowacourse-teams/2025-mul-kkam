package com.mulkkam.ui.settingwater.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.viewbinding.ViewBinding

class SettingWaterAdapter(
    private val handler: Handler,
) : ListAdapter<SettingWaterItem, SettingWaterViewHolder<out SettingWaterItem, out ViewBinding>>(SettingWaterDiffCallback) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): SettingWaterViewHolder<out SettingWaterItem, out ViewBinding> =
        when (SettingWaterViewType.entries[viewType]) {
            SettingWaterViewType.CUP -> CupViewHolder(parent, handler)
            SettingWaterViewType.ADD -> AddViewHolder(parent, handler)
        }

    override fun onBindViewHolder(
        holder: SettingWaterViewHolder<out SettingWaterItem, out ViewBinding>,
        position: Int,
    ) {
        val item = getItem(position)

        when {
            holder is CupViewHolder && item is SettingWaterItem.CupItem -> holder.bind(item)
            holder is AddViewHolder && item is SettingWaterItem.AddItem -> holder.bind(item)
        }
    }

    override fun getItemCount(): Int = currentList.size

    override fun getItemViewType(position: Int): Int = getItem(position).viewType.ordinal

    interface Handler :
        CupViewHolder.Handler,
        AddViewHolder.Handler
}
