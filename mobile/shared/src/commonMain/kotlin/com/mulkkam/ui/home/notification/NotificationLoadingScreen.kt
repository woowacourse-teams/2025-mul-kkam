package com.mulkkam.ui.home.notification

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mulkkam.ui.home.notification.component.NotificationShimmerItem
import com.mulkkam.ui.util.LoadingShimmerEffect

@Composable
fun NotificationLoadingScreen(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        repeat(7) {
            LoadingShimmerEffect {
                NotificationShimmerItem(
                    it,
                )
            }
        }
    }
}
