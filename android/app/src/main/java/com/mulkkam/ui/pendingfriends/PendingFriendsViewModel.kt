package com.mulkkam.ui.pendingfriends

import androidx.lifecycle.ViewModel
import com.mulkkam.domain.model.friends.PendingFriend
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDateTime

class PendingFriendsViewModel : ViewModel() {
    private val _receivedRequest: MutableStateFlow<List<PendingFriend>> = MutableStateFlow(listOf())
    val receivedRequest: StateFlow<List<PendingFriend>> = _receivedRequest.asStateFlow()

    private val _sentRequest: MutableStateFlow<List<PendingFriend>> = MutableStateFlow(listOf())
    val sentRequest: StateFlow<List<PendingFriend>> = _sentRequest.asStateFlow()

    init {
        loadPendingFriends()
    }

    private fun loadPendingFriends() {
        _receivedRequest.value =
            listOf(
                PendingFriend("hwannow", LocalDateTime.of(2025, 10, 10, 12, 0)),
                PendingFriend("GongBaek", LocalDateTime.of(2025, 10, 10, 13, 0)),
                PendingFriend("Eden", LocalDateTime.of(2025, 10, 13, 15, 0)),
            )

        _sentRequest.value =
            listOf(
                PendingFriend("hwannow", LocalDateTime.of(2025, 10, 10, 12, 0)),
                PendingFriend("GongBaek", LocalDateTime.of(2025, 10, 10, 13, 0)),
                PendingFriend("Eden", LocalDateTime.of(2025, 10, 13, 15, 0)),
            )
    }

    fun acceptFriend() {}

    fun rejectFriend() {}

    fun cancelRequest() {}
}
