package com.mulkkam.ui.friends

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.mulkkam.ui.designsystem.MulkkamTheme
import com.mulkkam.ui.main.Refreshable
import com.mulkkam.ui.pendingfriends.PendingFriendsActivity
import com.mulkkam.ui.searchmembers.SearchMembersActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FriendsFragment :
    Fragment(),
    Refreshable {
    private val viewModel: FriendsViewModel by viewModels()
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private var activityResultSource: ActivityResultSource? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initActivityResultLauncher()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val composeView =
            ComposeView(requireContext()).apply {
                setContent {
                    MulkkamTheme {
                        FriendsScreen(
                            navigateToSearch = {
                                val intent = SearchMembersActivity.newIntent(requireContext())
                                activityResultSource = ActivityResultSource.SEARCH_MEMBERS
                                activityResultLauncher.launch(intent)
                            },
                            navigateToFriendRequests = {
                                val intent = PendingFriendsActivity.newIntent(requireContext())
                                activityResultSource = ActivityResultSource.PENDING_FRIENDS
                                activityResultLauncher.launch(intent)
                            },
                            viewModel = viewModel,
                        )
                    }
                }
            }
        return composeView
    }

    private fun initActivityResultLauncher() {
        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                val source = activityResultSource
                activityResultSource = null
                if (source == null) return@registerForActivityResult

                val isSuccess = result.resultCode == source.successCode
                val isAccepted = result.data?.getBooleanExtra(source.acceptedExtraKey, false) ?: false

                if (isSuccess && isAccepted) {
                    refreshFriends()
                }
            }
    }

    private fun refreshFriends() {
        viewModel.loadFriends()
        viewModel.loadFriendRequestCount()
    }

    override fun onReselected() {
        super.onReselected()
        refreshFriends()
    }

    private enum class ActivityResultSource(
        val successCode: Int,
        val acceptedExtraKey: String,
    ) {
        SEARCH_MEMBERS(
            successCode = SearchMembersActivity.RESULT_CODE_FRIEND_ACCEPTED,
            acceptedExtraKey = SearchMembersActivity.EXTRA_KEY_IS_FRIEND_ACCEPTED,
        ),
        PENDING_FRIENDS(
            successCode = PendingFriendsActivity.RESULT_CODE_FRIEND_ACCEPTED,
            acceptedExtraKey = PendingFriendsActivity.EXTRA_KEY_IS_FRIEND_ACCEPTED,
        ),
    }
}
