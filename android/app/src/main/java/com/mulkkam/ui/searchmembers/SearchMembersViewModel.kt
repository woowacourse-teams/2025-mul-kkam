package com.mulkkam.ui.searchmembers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.domain.model.friends.FriendRequestStatus
import com.mulkkam.domain.model.members.MemberSearchInfo
import com.mulkkam.domain.model.members.RequestDirection
import com.mulkkam.domain.model.result.toMulKkamError
import com.mulkkam.domain.repository.FriendsRepository
import com.mulkkam.domain.repository.MembersRepository
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.model.MulKkamUiState.Idle.toSuccessDataOrNull
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchMembersViewModel
    @Inject
    constructor(
        private val friendsRepository: FriendsRepository,
        private val membersRepository: MembersRepository,
    ) : ViewModel() {
        private var lastId: Long? = null

        private val _name: MutableStateFlow<String> = MutableStateFlow("")
        val name: StateFlow<String> = _name.asStateFlow()

        private val _memberSearchUiState: MutableStateFlow<MulKkamUiState<List<MemberSearchInfo>>> =
            MutableStateFlow(MulKkamUiState.Idle)
        val memberSearchUiState: StateFlow<MulKkamUiState<List<MemberSearchInfo>>> =
            _memberSearchUiState.asStateFlow()

        private val _loadUiState: MutableStateFlow<MulKkamUiState<Unit>> =
            MutableStateFlow(MulKkamUiState.Idle)
        val loadUiState: StateFlow<MulKkamUiState<Unit>> = _loadUiState.asStateFlow()

        private val _isTyping: MutableStateFlow<Boolean> = MutableStateFlow(false)
        val isTyping: StateFlow<Boolean> = _isTyping.asStateFlow()

        private val _onRequestFriends: MutableSharedFlow<MulKkamUiState<Unit>> =
            MutableSharedFlow<MulKkamUiState<Unit>>()
        val onRequestFriends: SharedFlow<MulKkamUiState<Unit>> = _onRequestFriends.asSharedFlow()

        private val _onAcceptFriends: MutableSharedFlow<MulKkamUiState<String>> =
            MutableSharedFlow<MulKkamUiState<String>>()
        val onAcceptFriends: SharedFlow<MulKkamUiState<String>> = _onAcceptFriends.asSharedFlow()

        private val _receivedMemberSearchInfo: MutableSharedFlow<MulKkamUiState<MemberSearchInfo>> =
            MutableSharedFlow()
        val receivedMemberSearchInfo: SharedFlow<MulKkamUiState<MemberSearchInfo>> = _receivedMemberSearchInfo.asSharedFlow()

        init {
            viewModelScope.launch {
                _name.debounce(300L).collect { query ->
                    searchMembers()
                }
            }
        }

        fun updateName(newName: String) {
            _isTyping.value = name.value != newName
            _name.value = newName
            lastId = null
        }

        private fun searchMembers() {
            viewModelScope.launch {
                runCatching {
                    membersRepository.getMembersSearch(name.value, lastId, SEARCH_SIZE).getOrError()
                }.onSuccess { memberSearchResult ->
                    _memberSearchUiState.value =
                        MulKkamUiState.Success(memberSearchResult.memberSearchInfos)
                    lastId = memberSearchResult.nextId
                    _isTyping.value = false
                }
            }
        }

        fun loadMoreMembers() {
            if (lastId == null) return
            viewModelScope.launch {
                runCatching {
                    _loadUiState.value = MulKkamUiState.Loading
                    membersRepository.getMembersSearch(name.value, lastId, SEARCH_SIZE).getOrError()
                }.onSuccess { memberSearchResult ->
                    _loadUiState.value = MulKkamUiState.Success(Unit)
                    val currentList = memberSearchUiState.value.toSuccessDataOrNull() ?: emptyList()
                    val updatedList = currentList + memberSearchResult.memberSearchInfos
                    _memberSearchUiState.value = MulKkamUiState.Success(updatedList)
                    lastId = memberSearchResult.nextId
                }.onFailure {
                    _loadUiState.value = MulKkamUiState.Failure(it.toMulKkamError())
                }
            }
        }

        fun requestFriends(memberSearchInfo: MemberSearchInfo) {
            viewModelScope.launch {
                if (memberSearchInfo.isRequestedToMe()) {
                    _receivedMemberSearchInfo.emit(MulKkamUiState.Success(memberSearchInfo))
                    return@launch
                }
                sendFriendRequest(memberSearchInfo.id)
            }
        }

        fun acceptFriendRequest(memberSearchInfo: MemberSearchInfo) {
            viewModelScope.launch {
                runCatching {
                    friendsRepository
                        .patchFriendRequest(memberSearchInfo.id, FriendRequestStatus.ACCEPTED)
                        .getOrError()
                }.onSuccess {
                    _onAcceptFriends.emit(MulKkamUiState.Success(memberSearchInfo.nickname.name))
                    updateSearchMembersUiState(
                        memberSearchInfo.id,
                        FriendRequestStatus.ACCEPTED,
                        RequestDirection.REQUESTED_TO_ME,
                    )
                }.onFailure {
                    _onAcceptFriends.emit(MulKkamUiState.Failure(it.toMulKkamError()))
                }
            }
        }

        private suspend fun sendFriendRequest(id: Long) {
            runCatching {
                friendsRepository.postFriendRequest(id).getOrError()
            }.onSuccess {
                _onRequestFriends.emit(MulKkamUiState.Success(Unit))
                updateSearchMembersUiState(id, FriendRequestStatus.REQUESTED, RequestDirection.REQUESTED_BY_ME)
            }.onFailure {
                _onRequestFriends.emit(MulKkamUiState.Failure(it.toMulKkamError()))
            }
        }

        private fun updateSearchMembersUiState(
            id: Long,
            status: FriendRequestStatus,
            direction: RequestDirection,
        ) {
            val currentList = _memberSearchUiState.value.toSuccessDataOrNull() ?: return
            val updatedList =
                currentList.map { member ->
                    if (member.id == id) {
                        member.copy(
                            status = status,
                            direction = direction,
                        )
                    } else {
                        member
                    }
                }

            _memberSearchUiState.value = MulKkamUiState.Success(updatedList)
        }

        companion object {
            private const val SEARCH_SIZE: Int = 20
        }
    }
