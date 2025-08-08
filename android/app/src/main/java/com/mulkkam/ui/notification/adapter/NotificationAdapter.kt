package com.mulkkam.ui.notification.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mulkkam.domain.model.Notification
import com.mulkkam.ui.notification.adapter.NotificationViewHolder.NotificationHandler

class NotificationAdapter(
    private val handler: NotificationHandler,
) : RecyclerView.Adapter<NotificationViewHolder>() {
    private val notifications: MutableList<Notification> = mutableListOf()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): NotificationViewHolder = NotificationViewHolder.from(parent, handler)

    override fun onBindViewHolder(
        holder: NotificationViewHolder,
        position: Int,
    ) {
        holder.bind(notifications[position])
    }

    override fun getItemCount(): Int = notifications.size

    fun changeItems(newNotifications: List<Notification>) {
        val oldSize = notifications.size
        val newSize = newNotifications.size
        notifications.clear()
        notifications.addAll(newNotifications)

        notifyItemRangeChanged(0, oldSize)
        if (oldSize > newSize) {
            notifyItemRangeRemoved(newSize, oldSize - newSize)
        } else {
            notifyItemRangeInserted(oldSize, newSize - oldSize)
        }
    }
}
