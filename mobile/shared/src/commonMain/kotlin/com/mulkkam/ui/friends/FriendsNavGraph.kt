package com.mulkkam.ui.friends

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import com.mulkkam.ui.friends.friends.FriendsRoute
import com.mulkkam.ui.friends.friends.FriendsScreen
import com.mulkkam.ui.friends.pendingfriends.PendingFriendsRoute
import com.mulkkam.ui.friends.pendingfriends.PendingFriendsScreen
import com.mulkkam.ui.friends.searchmembers.SearchMembersRoute
import com.mulkkam.ui.friends.searchmembers.SearchMembersScreen
import com.mulkkam.ui.navigation.FriendsRoute
import com.mulkkam.ui.navigation.MainNavigator
import com.mulkkam.ui.navigation.NavEntry
import com.mulkkam.ui.navigation.entry

object FriendsNavGraph {
    @Composable
    fun entryProvider(
        route: FriendsRoute,
        padding: PaddingValues,
        navigator: MainNavigator,
    ): NavEntry<FriendsRoute> =
        when (route) {
            is FriendsRoute.Friends -> {
                entry(route) {
                    FriendsRoute(
                        padding = padding,
                        onNavigateToPendingFriends = navigator::navigateToPendingFriends,
                        onNavigateToSearchMembers = navigator::navigateToSearchMembers,
                    )
                }
            }

            is FriendsRoute.PendingFriends -> {
                entry(route) {
                    PendingFriendsRoute(
                        padding = padding,
                        onNavigateToBack = navigator::popBackStack,
                    )
                }
            }

            is FriendsRoute.SearchMembers -> {
                entry(route) {
                    SearchMembersRoute(
                        padding = padding,
                        onNavigateToBack = navigator::popBackStack,
                    )
                }
            }
        }
}
