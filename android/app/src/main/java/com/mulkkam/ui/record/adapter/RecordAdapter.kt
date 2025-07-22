package com.mulkkam.ui.record.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mulkkam.domain.WaterRecord

class RecordAdapter : RecyclerView.Adapter<WaterRecordViewHolder>() {
    private val waterRecords: MutableList<WaterRecord> = mutableListOf()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): WaterRecordViewHolder = WaterRecordViewHolder.from(parent)

    override fun onBindViewHolder(
        holder: WaterRecordViewHolder,
        position: Int,
    ) {
        holder.bind(waterRecords[position])
    }

    override fun getItemCount(): Int = waterRecords.size

    fun changeItems(newWaterRecords: List<WaterRecord>) {
        val oldSize = waterRecords.size
        val newSize = newWaterRecords.size
        waterRecords.clear()
        waterRecords.addAll(newWaterRecords)

        notifyItemRangeChanged(0, oldSize)
        if (oldSize > newSize) {
            notifyItemRangeRemoved(newSize, oldSize - newSize)
        } else {
            notifyItemRangeInserted(oldSize, newSize - oldSize)
        }
    }
}
