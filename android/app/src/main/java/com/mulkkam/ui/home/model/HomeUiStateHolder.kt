package com.mulkkam.ui.home.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Stable
class HomeUiStateHolder internal constructor(
    private val scope: CoroutineScope,
    private val drinkAnimationMillis: Long,
) {
    var isDrinking by mutableStateOf(false)
        private set

    var playConfetti by mutableStateOf(false)
        private set

    var playFriendWaterBalloonExplode by mutableStateOf(false)
        private set

    fun triggerDrinkAnimation() {
        if (isDrinking) return
        scope.launch {
            isDrinking = true
            delay(drinkAnimationMillis)
            isDrinking = false
        }
    }

    fun triggerConfettiOnce() {
        playConfetti = true
    }

    fun onConfettiFinished() {
        playConfetti = false
    }

    fun triggerFriendWaterBalloonExplode() {
        playFriendWaterBalloonExplode = true
    }

    fun onFriendWaterBalloonExplodeFinished() {
        playFriendWaterBalloonExplode = false
    }
}

@Composable
fun rememberHomeUiStateHolder(drinkAnimationMillis: Long = 2000L): HomeUiStateHolder {
    val scope = rememberCoroutineScope()
    return remember(drinkAnimationMillis) {
        HomeUiStateHolder(
            scope = scope,
            drinkAnimationMillis = drinkAnimationMillis,
        )
    }
}
