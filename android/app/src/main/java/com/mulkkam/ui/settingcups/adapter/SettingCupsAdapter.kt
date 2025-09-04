package com.mulkkam.ui.settingcups.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.viewbinding.ViewBinding

class SettingCupsAdapter(
    private val handler: Handler,
) : ListAdapter<SettingCupsItem, SettingCupsViewHolder<out SettingCupsItem, out ViewBinding>>(SettingCupsDiffCallback),
    ItemReorderListener {
    private val mutableItems = mutableListOf<SettingCupsItem>()

    override fun submitList(list: List<SettingCupsItem>?) {
        mutableItems.clear()
        if (list != null) mutableItems.addAll(list)
        super.submitList(list)
    }

    override fun onItemMove(
        fromPosition: Int,
        toPosition: Int,
    ) {
        val movedItem = mutableItems.removeAt(fromPosition)
        mutableItems.add(toPosition, movedItem)
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onItemDrop() {
        val cupsOrder = mutableItems.filterIsInstance<SettingCupsItem.CupItem>()
        handler.onCupsOrderChanged(cupsOrder)
    }

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

    override fun getItemViewType(position: Int): Int = getItem(position).viewType.ordinal

    interface Handler :
        CupViewHolder.Handler,
        AddViewHolder.Handler {
        fun onCupsOrderChanged(newOrder: List<SettingCupsItem.CupItem>)
    }
}
