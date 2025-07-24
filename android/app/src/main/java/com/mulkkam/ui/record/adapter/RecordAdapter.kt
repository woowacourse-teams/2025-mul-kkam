package com.mulkkam.ui.record.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mulkkam.domain.IntakeHistory

class RecordAdapter : RecyclerView.Adapter<RecordViewHolder>() {
    private val intakeHistories: MutableList<IntakeHistory> = mutableListOf()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecordViewHolder = RecordViewHolder.from(parent)

    override fun onBindViewHolder(
        holder: RecordViewHolder,
        position: Int,
    ) {
        holder.bind(intakeHistories[position])
    }

    override fun getItemCount(): Int = intakeHistories.size

    fun changeItems(newIntakeHistories: List<IntakeHistory>) {
        val oldSize = intakeHistories.size
        val newSize = newIntakeHistories.size
        intakeHistories.clear()
        intakeHistories.addAll(newIntakeHistories)

        notifyItemRangeChanged(0, oldSize)
        if (oldSize > newSize) {
            notifyItemRangeRemoved(newSize, oldSize - newSize)
        } else {
            notifyItemRangeInserted(oldSize, newSize - oldSize)
        }
    }
}
