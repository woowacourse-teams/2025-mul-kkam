package com.mulkkam.ui.record.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mulkkam.domain.WaterRecord

class RecordAdapter(
    private val waterRecords: List<WaterRecord>,
) : RecyclerView.Adapter<WaterRecordViewHolder>() {
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
}
