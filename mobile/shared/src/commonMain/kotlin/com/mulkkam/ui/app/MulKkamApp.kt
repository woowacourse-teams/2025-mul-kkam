package com.mulkkam.ui.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mulkkam.ui.navigation.MainNavHost
import com.mulkkam.ui.navigation.rememberMainNavigator

@Composable
fun MulKkamApp() {
    val navigator = rememberMainNavigator()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) { innerPadding ->
        MainNavHost(
            navigator = navigator,
            padding = innerPadding,
            modifier = Modifier.padding(innerPadding),
        )
    }
}
