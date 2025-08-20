package com.mulkkam.ui.notification.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mulkkam.domain.model.notification.Notification
import com.mulkkam.ui.notification.adapter.NotificationViewHolder.NotificationApplyHandler

class NotificationAdapter(
    private val applyHandler: NotificationApplyHandler,
    private val deleteHandler: NotificationDeleteHandler,
) : RecyclerView.Adapter<NotificationViewHolder>(),
    ItemSwipeListener {
    val notifications: MutableList<Notification> = mutableListOf()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): NotificationViewHolder = NotificationViewHolder.from(parent, applyHandler)

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

    override fun onItemSwipe(position: Int) {
        deleteHandler.onDelete(notifications[position].id)
        notifications.removeAt(position)
        notifyItemRemoved(position)
    }

    fun interface NotificationDeleteHandler {
        fun onDelete(id: Int)
    }
}
