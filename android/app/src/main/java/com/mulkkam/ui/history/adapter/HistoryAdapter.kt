package com.mulkkam.ui.history.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mulkkam.domain.IntakeHistory
import com.mulkkam.ui.history.adapter.HistoryViewHolder.OnItemLongClickListener

class HistoryAdapter : RecyclerView.Adapter<HistoryViewHolder>() {
    private val intakeHistories: MutableList<IntakeHistory> = mutableListOf()
    var onItemLongClickListener: OnItemLongClickListener? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): HistoryViewHolder = HistoryViewHolder.from(parent, onItemLongClickListener)

    override fun onBindViewHolder(
        holder: HistoryViewHolder,
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
