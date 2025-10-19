package com.mulkkam.ui.pendingfriends

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.di.RepositoryInjection.friendsRepository
import com.mulkkam.domain.model.friends.FriendsRequestInfo
import com.mulkkam.domain.model.friends.FriendsStatus
import com.mulkkam.domain.model.result.toMulKkamError
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.model.MulKkamUiState.Idle.toSuccessDataOrNull
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PendingFriendsViewModel : ViewModel() {
    private val _receivedRequests: MutableStateFlow<MulKkamUiState<List<FriendsRequestInfo>>> =
        MutableStateFlow(MulKkamUiState.Idle)
    val receivedRequests: StateFlow<MulKkamUiState<List<FriendsRequestInfo>>> =
        _receivedRequests.asStateFlow()

    private val _sentRequests: MutableStateFlow<MulKkamUiState<List<FriendsRequestInfo>>> =
        MutableStateFlow(MulKkamUiState.Idle)
    val sentRequests: StateFlow<MulKkamUiState<List<FriendsRequestInfo>>> =
        _sentRequests.asStateFlow()

    private val _onAcceptRequest: MutableSharedFlow<MulKkamUiState<String>> =
        MutableSharedFlow()
    val onAcceptRequest: SharedFlow<MulKkamUiState<String>> = _onAcceptRequest.asSharedFlow()

    private val _onRejectRequest: MutableSharedFlow<MulKkamUiState<Unit>> =
        MutableSharedFlow()
    val onRejectRequest: SharedFlow<MulKkamUiState<Unit>> = _onRejectRequest.asSharedFlow()

    private val _onCancelRequest: MutableSharedFlow<MulKkamUiState<Unit>> =
        MutableSharedFlow()
    val onCancelRequest: SharedFlow<MulKkamUiState<Unit>> = _onCancelRequest.asSharedFlow()

    private var receivedLastId: Long? = null
    private var sentLastId: Long? = null

    init {
        loadReceivedFriendsRequest()
        loadSentFriendsRequest()
    }

    private fun loadReceivedFriendsRequest() {
        viewModelScope.launch {
            runCatching {
                friendsRepository.getFriendsRequestReceived(size = REQUEST_SIZE).getOrError()
            }.onSuccess { friendsRequestResult ->
                _receivedRequests.value =
                    MulKkamUiState.Success(friendsRequestResult.friendsRequestInfos)
                receivedLastId = friendsRequestResult.nextId
            }.onFailure {
                _receivedRequests.value = MulKkamUiState.Failure(it.toMulKkamError())
            }
        }
    }

    private fun loadSentFriendsRequest() {
        viewModelScope.launch {
            runCatching {
                friendsRepository.getFriendsRequestSent(size = REQUEST_SIZE).getOrError()
            }.onSuccess { friendsRequestResult ->
                _sentRequests.value =
                    MulKkamUiState.Success(friendsRequestResult.friendsRequestInfos)
                sentLastId = friendsRequestResult.nextId
            }.onFailure {
                _sentRequests.value = MulKkamUiState.Failure(it.toMulKkamError())
            }
        }
    }

    fun acceptFriend(friendsRequest: FriendsRequestInfo) {
        viewModelScope.launch {
            runCatching {
                friendsRepository
                    .patchFriendsRequest(
                        friendsRequest.memberId,
                        FriendsStatus.ACCEPTED,
                    ).getOrError()
            }.onSuccess {
                _onAcceptRequest.emit(MulKkamUiState.Success(friendsRequest.nickname.name))
                val currentList = receivedRequests.value.toSuccessDataOrNull() ?: return@launch
                _receivedRequests.value =
                    MulKkamUiState.Success(currentList.filter { it != friendsRequest })
            }.onFailure {
                _onAcceptRequest.emit(MulKkamUiState.Failure(it.toMulKkamError()))
            }
        }
    }

    fun rejectFriend(friendsRequest: FriendsRequestInfo) {
        viewModelScope.launch {
            runCatching {
                friendsRepository
                    .patchFriendsRequest(
                        friendsRequest.memberId,
                        FriendsStatus.REJECTED,
                    ).getOrError()
            }.onSuccess {
                _onRejectRequest.emit(MulKkamUiState.Success(Unit))
                val currentList = receivedRequests.value.toSuccessDataOrNull() ?: return@launch
                _receivedRequests.value =
                    MulKkamUiState.Success(currentList.filter { it != friendsRequest })
            }.onFailure {
                _onRejectRequest.emit(MulKkamUiState.Failure(it.toMulKkamError()))
            }
        }
    }

    fun cancelRequest(friendsRequest: FriendsRequestInfo) {
        viewModelScope.launch {
            runCatching {
                friendsRepository.deleteFriendsRequest(friendsRequest.memberId).getOrError()
            }.onSuccess {
                _onCancelRequest.emit(MulKkamUiState.Success(Unit))
                val currentList = sentRequests.value.toSuccessDataOrNull() ?: return@launch
                _sentRequests.value =
                    MulKkamUiState.Success(currentList.filter { it != friendsRequest })
            }.onFailure {
                _onCancelRequest.emit(MulKkamUiState.Failure(it.toMulKkamError()))
            }
        }
    }

    fun loadMoreReceivedFriendsRequest() {
        if (receivedLastId == null) return
        viewModelScope.launch {
            runCatching {
                friendsRepository
                    .getFriendsRequestReceived(
                        lastId = receivedLastId,
                        size = REQUEST_SIZE,
                    ).getOrError()
            }.onSuccess {
                val currentList = receivedRequests.value.toSuccessDataOrNull() ?: return@launch
                _receivedRequests.value =
                    MulKkamUiState.Success(currentList + it.friendsRequestInfos)
                receivedLastId = it.nextId
            }
        }
    }

    fun loadMoreSentFriendsRequest() {
        if (sentLastId == null) return
        viewModelScope.launch {
            runCatching {
                friendsRepository
                    .getFriendsRequestSent(
                        lastId = sentLastId,
                        size = REQUEST_SIZE,
                    ).getOrError()
            }.onSuccess {
                val currentList = sentRequests.value.toSuccessDataOrNull() ?: return@launch
                _sentRequests.value =
                    MulKkamUiState.Success(currentList + it.friendsRequestInfos)
                sentLastId = it.nextId
            }
        }
    }

    companion object {
        private const val REQUEST_SIZE: Int = 20
    }
}
