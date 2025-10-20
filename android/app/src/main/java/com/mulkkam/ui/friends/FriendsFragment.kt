package com.mulkkam.ui.friends

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.mulkkam.ui.designsystem.MulkkamTheme

class FriendsFragment : Fragment() {
    private val viewModel: FriendsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        super.onCreate(savedInstanceState)
        val composeView =
            ComposeView(requireContext()).apply {
                setContent {
                    MulkkamTheme {
                        FriendsScreen(
                            navigateToSearch = {
                                // TODO: 친구 검색으로 이동하는 기능 구현
                            },
                            navigateToFriendRequests = {
                                // TODO: 친구 요청 목록으로 이동하는 기능 구현
                            },
                            viewModel = viewModel,
                        )
                    }
                }
            }
        return composeView
    }
}
