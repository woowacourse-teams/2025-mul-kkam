package com.mulkkam.ui.settingcups.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.viewbinding.ViewBinding

class SettingCupsAdapter(
    private val handler: Handler,
) : ListAdapter<SettingCupsItem, SettingCupsViewHolder<out SettingCupsItem, out ViewBinding>>(SettingCupsDiffCallback) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): SettingCupsViewHolder<out SettingCupsItem, out ViewBinding> =
        when (SettingCupsViewType.entries[viewType]) {
            SettingCupsViewType.CUP -> CupViewHolder(parent, handler)
            SettingCupsViewType.ADD -> AddViewHolder(parent, handler)
        }

    override fun onBindViewHolder(
        holder: SettingCupsViewHolder<out SettingCupsItem, out ViewBinding>,
        position: Int,
    ) {
        val item = getItem(position)

        when {
            holder is CupViewHolder && item is SettingCupsItem.CupItem -> holder.bind(item)
            holder is AddViewHolder && item is SettingCupsItem.AddItem -> holder.bind(item)
        }
    }

    override fun getItemCount(): Int = currentList.size

    override fun getItemViewType(position: Int): Int = getItem(position).viewType.ordinal

    interface Handler :
        CupViewHolder.Handler,
        AddViewHolder.Handler
}
