package com.mulkkam.ui.pendingfriends

import androidx.lifecycle.ViewModel
import com.mulkkam.domain.model.friends.PendingFriend
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDateTime

class PendingFriendsViewModel : ViewModel() {
    private val _pendingFriends: MutableStateFlow<List<PendingFriend>> = MutableStateFlow(listOf())
    val pendingFriends: StateFlow<List<PendingFriend>> = _pendingFriends.asStateFlow()

    init {
        loadPendingFriends()
    }

    private fun loadPendingFriends() {
        _pendingFriends.value =
            listOf(
                PendingFriend("hwannow", LocalDateTime.of(2025, 10, 10, 12, 0)),
                PendingFriend("GongBaek", LocalDateTime.of(2025, 10, 10, 13, 0)),
                PendingFriend("Eden", LocalDateTime.of(2025, 10, 13, 15, 0)),
            )
    }

    fun acceptFriend() {}

    fun rejectFriend() {}
}
