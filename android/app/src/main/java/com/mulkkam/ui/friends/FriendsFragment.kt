package com.mulkkam.ui.friends

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.mulkkam.ui.designsystem.MulkkamTheme
import com.mulkkam.ui.pendingfriends.PendingFriendsActivity
import com.mulkkam.ui.searchmembers.SearchMembersActivity

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
                                val intent = SearchMembersActivity.newIntent(requireContext())
                                startActivity(intent)
                            },
                            navigateToFriendRequests = {
                                val intent = PendingFriendsActivity.newIntent(requireContext())
                                startActivity(intent)
                            },
                            viewModel = viewModel,
                        )
                    }
                }
            }
        return composeView
    }
}
