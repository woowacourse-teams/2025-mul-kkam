package com.mulkkam

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.mulkkam.ui.auth.login.model.AuthPlatform
import com.mulkkam.ui.component.MulKkamSnackbarHost
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.White
import com.mulkkam.ui.main.component.MainBottomNavigationBar
import com.mulkkam.ui.main.model.MainTab
import com.mulkkam.ui.navigation.MainNavHost
import com.mulkkam.ui.navigation.rememberMainNavigator

@Composable
fun MulKkamApp(
    onLogin: (
        authPlatform: AuthPlatform,
        onSuccess: (token: String) -> Unit,
        onError: (errorMessage: String) -> Unit,
    ) -> Unit,
    onRegisterPushNotification: (
        onTokenUpdated: (token: String) -> Unit,
        onPermissionUpdated: (isGranted: Boolean) -> Unit,
        onError: (errorMessage: String) -> Unit,
    ) -> Unit,
    onRequestMainPermissions: () -> Unit,
) {
    val navigator = rememberMainNavigator()
    val snackbarHostState = remember { SnackbarHostState() }

    val currentTab: MainTab? by remember {
        derivedStateOf { MainTab.fromRoute(navigator.currentRoute) }
    }
    val showBottomNavigationBar: Boolean by remember {
        derivedStateOf { currentTab != null }
    }

    MulKkamTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = White,
            bottomBar = {
                if (showBottomNavigationBar) {
                    MainBottomNavigationBar(
                        selectedTab = currentTab ?: MainTab.DEFAULT,
                        onTabSelected = { tab ->
                            when (tab) {
                                MainTab.HOME -> navigator.navigateToHome()
                                MainTab.HISTORY -> navigator.navigateToHistory()
                                MainTab.FRIENDS -> navigator.navigateToFriends()
                                MainTab.SETTING -> navigator.navigateToSetting()
                            }
                        },
                    )
                }
            },
            snackbarHost = { MulKkamSnackbarHost(hostState = snackbarHostState) },
        ) { innerPadding ->
            MainNavHost(
                navigator = navigator,
                padding = innerPadding,
                onLogin = onLogin,
                onRegisterPushNotification = onRegisterPushNotification,
                onRequestMainPermissions = onRequestMainPermissions,
                snackbarHostState = snackbarHostState,
            )
        }
    }
}
