package com.mulkkam.ui.searchfriends

import androidx.lifecycle.ViewModel
import com.mulkkam.ui.model.MulKkamUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SearchMembersViewModel : ViewModel() {
    private val _searchFriendsUiState: MutableStateFlow<MulKkamUiState<List<Friend>>> =
        MutableStateFlow(
            MulKkamUiState.Success(
                listOf(
                    Friend("돈가스먹는환노", false),
                    Friend("돈가스먹는공백", true),
                    Friend("돈가스싫은이든", false),
                ),
            ),
        )
    val searchFriendsUiState: StateFlow<MulKkamUiState<List<Friend>>> = _searchFriendsUiState.asStateFlow()
}

data class Friend(
    val name: String,
    val isRequested: Boolean,
)
