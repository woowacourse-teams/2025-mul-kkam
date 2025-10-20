package com.mulkkam.ui.friends

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.di.RepositoryInjection.friendsRepository
import com.mulkkam.domain.model.friend.Friend
import com.mulkkam.domain.model.friend.FriendsResult
import com.mulkkam.domain.model.result.toMulKkamError
import com.mulkkam.ui.friends.model.FriendsDisplayMode
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.model.MulKkamUiState.Idle.toSuccessDataOrNull
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FriendsViewModel : ViewModel() {
    private val _friendsUiState: MutableStateFlow<MulKkamUiState<FriendsResult>> =
        MutableStateFlow(MulKkamUiState.Idle)
    val friendsUiState: StateFlow<MulKkamUiState<FriendsResult>> = _friendsUiState.asStateFlow()

    private val _friendRequestCountUiState: MutableStateFlow<MulKkamUiState<Int>> =
        MutableStateFlow(MulKkamUiState.Idle)
    val friendRequestCountUiState: StateFlow<MulKkamUiState<Int>> =
        _friendRequestCountUiState.asStateFlow()

    private val _loadMoreUiState: MutableStateFlow<MulKkamUiState<Unit>> =
        MutableStateFlow(MulKkamUiState.Idle)
    val loadMoreUiState: StateFlow<MulKkamUiState<Unit>> = _loadMoreUiState.asStateFlow()

    private val _displayMode: MutableStateFlow<FriendsDisplayMode> =
        MutableStateFlow(FriendsDisplayMode.VIEWING)
    val displayMode: StateFlow<FriendsDisplayMode> = _displayMode.asStateFlow()

    private val _hasMoreFriends: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val hasMoreFriends: StateFlow<Boolean> = _hasMoreFriends.asStateFlow()

    private var nextCursor: Long? = null

    init {
        loadFriends()
        loadFriendRequestCount()
    }

    fun loadFriends() {
        if (_friendsUiState.value == MulKkamUiState.Loading) return
        _loadMoreUiState.value = MulKkamUiState.Idle
        viewModelScope.launch {
            runCatching {
                _friendsUiState.value = MulKkamUiState.Loading
                friendsRepository
                    .getFriends(
                        lastId = null,
                        size = FRIENDS_PAGE_SIZE,
                    ).getOrError()
            }.onSuccess { friendsResult ->
                _friendsUiState.value = MulKkamUiState.Success(friendsResult)
                updatePagination(friendsResult)
            }.onFailure {
                _friendsUiState.value = MulKkamUiState.Failure(it.toMulKkamError())
            }
        }
    }

    fun loadFriendRequestCount() {
        if (_friendRequestCountUiState.value == MulKkamUiState.Loading) return
        viewModelScope.launch {
            runCatching {
                _friendRequestCountUiState.value = MulKkamUiState.Loading
                friendsRepository.getFriendRequestReceivedCount().getOrError()
            }.onSuccess { count ->
                _friendRequestCountUiState.value = MulKkamUiState.Success(count)
            }.onFailure {
                _friendRequestCountUiState.value = MulKkamUiState.Failure(it.toMulKkamError())
            }
        }
    }

    fun toggleDisplayMode() {
        _displayMode.value =
            when (displayMode.value) {
                FriendsDisplayMode.VIEWING -> FriendsDisplayMode.EDITING
                FriendsDisplayMode.EDITING -> FriendsDisplayMode.VIEWING
            }
    }

    fun loadMore() {
        val cursor: Long = nextCursor ?: return
        if (_loadMoreUiState.value == MulKkamUiState.Loading) return
        viewModelScope.launch {
            runCatching {
                _loadMoreUiState.value = MulKkamUiState.Loading
                friendsRepository
                    .getFriends(
                        lastId = cursor,
                        size = FRIENDS_PAGE_SIZE,
                    ).getOrError()
            }.onSuccess { friendsResult ->
                val currentFriends: FriendsResult? = friendsUiState.value.toSuccessDataOrNull()
                val combinedFriends: List<Friend> =
                    (currentFriends?.friends ?: emptyList()) + friendsResult.friends
                val updatedResult: FriendsResult =
                    friendsResult.copy(
                        friends = combinedFriends,
                    )
                _friendsUiState.value = MulKkamUiState.Success(updatedResult)
                _loadMoreUiState.value = MulKkamUiState.Success(Unit)
                updatePagination(updatedResult)
            }.onFailure {
                _loadMoreUiState.value = MulKkamUiState.Failure(it.toMulKkamError())
            }
        }
    }

    fun deleteFriend(friendId: Long) {
        val currentFriendsResult: FriendsResult =
            friendsUiState.value.toSuccessDataOrNull() ?: return
        val updatedFriends: List<Friend> = currentFriendsResult.friends.filterNot { it.id == friendId }
        _friendsUiState.value =
            MulKkamUiState.Success(
                currentFriendsResult.copy(
                    friends = updatedFriends,
                ),
            )
        updatePagination(
            currentFriendsResult.copy(
                friends = updatedFriends,
            ),
        )
        viewModelScope.launch {
            runCatching {
                friendsRepository.deleteFriend(friendId).getOrError()
            }.onSuccess {
                if (updatedFriends.isEmpty()) {
                    loadFriends()
                }
            }.onFailure {
                loadFriends()
            }
        }
    }

    private fun updatePagination(friendsResult: FriendsResult) {
        nextCursor =
            when {
                friendsResult.hasNext -> friendsResult.nextCursor
                else -> null
            }
        _hasMoreFriends.value = friendsResult.hasNext && friendsResult.nextCursor != null
    }

    companion object {
        private const val FRIENDS_PAGE_SIZE: Int = 20
    }
}
