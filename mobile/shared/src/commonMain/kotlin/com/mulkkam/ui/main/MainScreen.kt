package com.mulkkam.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.mulkkam.ui.designsystem.White
import com.mulkkam.ui.friends.friends.FriendsScreen
import com.mulkkam.ui.history.history.HistoryScreen
import com.mulkkam.ui.home.home.HomeScreen
import com.mulkkam.ui.main.component.MulKkamBottomNavigationBar
import com.mulkkam.ui.main.model.MainTab
import com.mulkkam.ui.navigation.MainNavigator
import com.mulkkam.ui.setting.setting.SettingRoute

@Composable
fun MainScreen(
    navigator: MainNavigator,
    modifier: Modifier = Modifier,
) {
    var selectedTab by rememberSaveable { mutableStateOf(MainTab.HOME) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = White,
        bottomBar = {
            MulKkamBottomNavigationBar(
                selectedTab = selectedTab,
                onTabSelected = { tab -> selectedTab = tab },
            )
        },
    ) { innerPadding ->
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
        ) {
            when (selectedTab) {
                MainTab.HOME -> {
                    HomeScreen(
                        navigateToNotification = navigator::navigateToHomeNotification,
                        onManualDrink = {}, // TODO: ManualDrink 연결
                    )
                }

                MainTab.HISTORY -> {
                    HistoryScreen(
                        padding = PaddingValues(),
                    )
                }

                MainTab.FRIENDS -> {
                    FriendsScreen(
                        navigateToSearch = navigator::navigateToSearchMembers,
                        navigateToFriendRequests = navigator::navigateToPendingFriends,
                    )
                }

                MainTab.SETTING -> {
                    SettingRoute(
                        onNavigateToAccountInfo = navigator::navigateToAccountInfo,
                        onNavigateToBioInfo = navigator::navigateToSettingBioInfo,
                        onNavigateToCups = navigator::navigateToSettingCups,
                        onNavigateToFeedback = navigator::navigateToFeedback,
                        onNavigateToNickname = navigator::navigateToSettingNickname,
                        onNavigateToNotification = navigator::navigateToSettingNotification,
                        onNavigateToReminder = navigator::navigateToReminder,
                        onNavigateToTargetAmount = navigator::navigateToSettingTargetAmount,
                        onNavigateToTerms = navigator::navigateToSettingTerms,
                    )
                }
            }
        }
    }
}
