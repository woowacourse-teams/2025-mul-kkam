package com.mulkkam.ui.searchmembers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.di.RepositoryInjection.membersRepository
import com.mulkkam.domain.model.members.MemberSearchInfo
import com.mulkkam.domain.model.result.toMulKkamError
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.model.MulKkamUiState.Idle.toSuccessDataOrNull
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class SearchMembersViewModel : ViewModel() {
    private var lastId: Long? = null

    private val _name: MutableStateFlow<String> = MutableStateFlow("")
    val name: StateFlow<String> = _name.asStateFlow()

    private val _searchMembersUiState: MutableStateFlow<MulKkamUiState<List<MemberSearchInfo>>> =
        MutableStateFlow(MulKkamUiState.Idle)
    val searchMembersUiState: StateFlow<MulKkamUiState<List<MemberSearchInfo>>> =
        _searchMembersUiState.asStateFlow()

    private val _loadUiState: MutableStateFlow<MulKkamUiState<Unit>> =
        MutableStateFlow(MulKkamUiState.Idle)
    val loadUiState: StateFlow<MulKkamUiState<Unit>> = _loadUiState.asStateFlow()

    private val _isTyping = MutableStateFlow(false)
    val isTyping = _isTyping.asStateFlow()

    init {
        viewModelScope.launch {
            _name.debounce(300L).collect { query ->
                searchMembers()
            }
        }
    }

    fun updateName(newName: String) {
        _isTyping.value = true
        _name.value = newName
        lastId = null
    }

    private fun searchMembers() {
        viewModelScope.launch {
            runCatching {
                membersRepository.getMembersSearch(name.value, lastId, SEARCH_SIZE).getOrError()
            }.onSuccess { memberSearchResult ->
                _searchMembersUiState.value =
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
                val currentList = searchMembersUiState.value.toSuccessDataOrNull() ?: emptyList()
                val updatedList = currentList + memberSearchResult.memberSearchInfos
                _searchMembersUiState.value = MulKkamUiState.Success(updatedList)
                lastId = memberSearchResult.nextId
            }.onFailure {
                _loadUiState.value = MulKkamUiState.Failure(it.toMulKkamError())
            }
        }
    }

    fun requestFriends(id: Long) {
        // TODO: 서버 연결 필요
    }

    companion object {
        private const val SEARCH_SIZE: Int = 20
    }
}
