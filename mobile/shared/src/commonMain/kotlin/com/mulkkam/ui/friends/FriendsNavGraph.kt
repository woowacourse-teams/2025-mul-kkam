package com.mulkkam.ui.friends

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import com.mulkkam.ui.navigation.FriendsRoute
import com.mulkkam.ui.navigation.MainNavigator

object FriendsNavGraph {
    @Composable
    fun entryProvider(
        route: com.mulkkam.ui.navigation.FriendsRoute,
        padding: PaddingValues,
        navigator: com.mulkkam.ui.navigation.MainNavigator,
    ): com.mulkkam.ui.core.NavEntry<com.mulkkam.ui.navigation.FriendsRoute> =
        when (route) {
            is com.mulkkam.ui.navigation.FriendsRoute.Friends -> {
                _root_ide_package_.com.mulkkam.ui.core.entry(route) {
                    _root_ide_package_.com.mulkkam.ui.friends.friends.FriendsScreen(
                        padding = padding,
                        onNavigateToPendingFriends = com.mulkkam.ui.navigation.MainNavigator::navigateToPendingFriends,
                        onNavigateToSearchMembers = com.mulkkam.ui.navigation.MainNavigator::navigateToSearchMembers,
                    )
                }
            }

            is com.mulkkam.ui.navigation.FriendsRoute.PendingFriends -> {
                _root_ide_package_.com.mulkkam.ui.core.entry(route) {
                    _root_ide_package_.com.mulkkam.ui.friends.pendingfriends.PendingFriendsScreen(
                        padding = padding,
                        onNavigateBack = com.mulkkam.ui.navigation.MainNavigator::popBackStack,
                    )
                }
            }

            is com.mulkkam.ui.navigation.FriendsRoute.SearchMembers -> {
                _root_ide_package_.com.mulkkam.ui.core.entry(route) {
                    _root_ide_package_.com.mulkkam.ui.friends.searchmembers.SearchMembersScreen(
                        padding = padding,
                        onNavigateBack = com.mulkkam.ui.navigation.MainNavigator::popBackStack,
                    )
                }
            }
        }
}
