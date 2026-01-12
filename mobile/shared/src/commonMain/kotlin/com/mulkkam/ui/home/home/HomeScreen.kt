package com.mulkkam.ui.home.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mulkkam.ui.designsystem.White
import com.mulkkam.ui.home.home.component.DrinkButton
import com.mulkkam.ui.home.home.component.FriendWaterBalloonExplodeLottie
import com.mulkkam.ui.home.home.component.HomeCharacter
import com.mulkkam.ui.home.home.component.HomeConfetti
import com.mulkkam.ui.home.home.component.HomeProgressOverview
import com.mulkkam.ui.home.home.component.HomeTopBar
import com.mulkkam.ui.home.home.model.HomeUiStateHolder
import com.mulkkam.ui.model.toSuccessDataOrNull

@Composable
fun HomeScreen(
    padding: PaddingValues,
    navigateToNotification: () -> Unit,
    onManualDrink: () -> Unit,
    snackbarHostState: SnackbarHostState,
    uiStateHolder: HomeUiStateHolder,
    viewModel: HomeViewModel,
) {
    val todayProgressUiState by viewModel.todayProgressInfoUiState.collectAsStateWithLifecycle()
    val cupsUiState by viewModel.cupsUiState.collectAsStateWithLifecycle()
    val alarmCountUiState by viewModel.alarmCountUiState.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize().padding(padding)) {
        Scaffold(
            topBar = {
                HomeTopBar(
                    alarmUnreadCount = alarmCountUiState.toSuccessDataOrNull() ?: 0L,
                    onNotificationClick = navigateToNotification,
                )
            },
            floatingActionButton = {
                DrinkButton(
                    cups = cupsUiState.toSuccessDataOrNull(),
                    onSelectCup = { cupId -> viewModel.addWaterIntakeByCup(cupId) },
                    onManual = onManualDrink,
                )
            },
        ) { padding ->
            val today = todayProgressUiState.toSuccessDataOrNull()
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(White)
                        .navigationBarsPadding()
                        .padding(padding),
            ) {
                HomeProgressOverview(
                    nickname = today?.nickname,
                    streak = today?.streak,
                    achievementRate = today?.achievementRate ?: 0f,
                    totalAmount = today?.totalAmount,
                    targetAmount = today?.targetAmount,
                )
                HomeCharacter(
                    isDrinking = uiStateHolder.isDrinking,
                    comment = today?.comment,
                    modifier = Modifier.weight(1f),
                )
            }

            HomeConfetti(
                playConfetti = uiStateHolder.playConfetti,
                onFinished = { uiStateHolder.onConfettiFinished() },
            )

            FriendWaterBalloonExplodeLottie(
                playConfetti = uiStateHolder.playFriendWaterBalloonExplode,
                onFinished = { uiStateHolder.onFriendWaterBalloonExplodeFinished() },
            )
        }
    }
}
