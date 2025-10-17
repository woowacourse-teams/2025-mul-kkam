package com.mulkkam.ui.searchmembers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.di.RepositoryInjection.membersRepository
import com.mulkkam.domain.model.members.MemberSearchInfo
import com.mulkkam.domain.model.result.toMulKkamError
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.model.MulKkamUiState.Idle.toSuccessDataOrNull
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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

    fun updateName(word: String) {
        _name.value = word
        lastId = null
    }

    fun searchMembers() {
        viewModelScope.launch {
            runCatching {
                membersRepository.getMembersSearch(name.value, lastId, SEARCH_SIZE).getOrError()
            }.onSuccess { memberSearchResult ->
                _searchMembersUiState.value =
                    MulKkamUiState.Success(memberSearchResult.memberSearchInfos)
                lastId = memberSearchResult.nextId
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

    fun requestFriends(id: Long) {}

    companion object {
        private const val SEARCH_SIZE: Int = 20
    }
}
