package com.mulkkam.ui.searchfriends

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mulkkam.R
import com.mulkkam.ui.designsystem.Gray100
import com.mulkkam.ui.designsystem.Gray300
import com.mulkkam.ui.designsystem.Gray400
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.MulkkamTheme
import com.mulkkam.ui.designsystem.White
import com.mulkkam.ui.model.MulKkamUiState.Idle.toSuccessDataOrNull
import com.mulkkam.ui.searchfriends.component.SearchMembersItem
import com.mulkkam.ui.searchfriends.component.SearchMembersTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchMembersScreen(
    navigateToBack: () -> Unit,
    viewModel: SearchMembersViewModel = viewModel(),
) {
    var name by remember { mutableStateOf("") }
    val searchFriendsUiState by viewModel.searchFriendsUiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { SearchMembersTopAppBar(navigateToBack) },
        containerColor = White,
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding),
        ) {
            BasicTextField(
                value = name,
                onValueChange = { name = it },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                        .height(44.dp)
                        .border(width = 1.dp, color = Gray400, shape = RoundedCornerShape(8.dp)),
                textStyle = MulKkamTheme.typography.body2.copy(color = Gray400),
                decorationBox = { innerTextField ->
                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            modifier = Modifier.padding(vertical = 4.dp, horizontal = 16.dp),
                            painter = painterResource(R.drawable.ic_search_friends_search),
                            contentDescription = null,
                            tint = Gray300,
                        )
                        innerTextField()
                    }
                },
            )

            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 1.dp,
                color = Gray100,
            )

            LazyColumn {
                val searchFriends = searchFriendsUiState.toSuccessDataOrNull() ?: return@LazyColumn

                items(
                    count = searchFriends.size,
                    key = { index -> searchFriends[index].name },
                ) { index ->
                    SearchMembersItem(
                        name = searchFriends[index].name,
                    )

                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth(),
                        thickness = 1.dp,
                        color = Gray100,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchMembersScreenPreview() {
    MulkkamTheme {
        SearchMembersScreen(navigateToBack = {})
    }
}
