package com.mulkkam.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mulkkam.ui.designsystem.MulkkamTheme
import com.mulkkam.ui.designsystem.White
import com.mulkkam.ui.home.component.DrinkButton
import com.mulkkam.ui.home.component.FriendWaterBalloonExplodeLottie
import com.mulkkam.ui.home.component.HomeCharacter
import com.mulkkam.ui.home.component.HomeConfetti
import com.mulkkam.ui.home.component.HomeProgressOverview
import com.mulkkam.ui.home.component.HomeTopBar
import com.mulkkam.ui.home.model.rememberHomeUiStateHolder
import com.mulkkam.ui.main.MainViewModel
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.model.MulKkamUiState.Loading.toSuccessDataOrNull
import com.mulkkam.ui.util.extensions.collectWithLifecycle

@Composable
fun HomeScreen(
    navigateToNotification: () -> Unit,
    onManualDrink: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
    parentViewModel: MainViewModel = viewModel(),
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val todayProgressUiState by viewModel.todayProgressInfoUiState.collectAsStateWithLifecycle()
    val cupsUiState by viewModel.cupsUiState.collectAsStateWithLifecycle()
    val alarmCountUiState by viewModel.alarmCountUiState.collectAsStateWithLifecycle()

    val uiStateHolder = rememberHomeUiStateHolder()

    viewModel.isGoalAchieved.collectWithLifecycle(lifecycleOwner) {
        uiStateHolder.triggerConfettiOnce()
    }

    viewModel.drinkUiState.collectWithLifecycle(lifecycleOwner) { state ->
        if (state is MulKkamUiState.Success) {
            uiStateHolder.triggerDrinkAnimation()
        }
    }

    parentViewModel.onReceiveFriendWaterBalloon.collectWithLifecycle(lifecycleOwner) {
        uiStateHolder.triggerFriendWaterBalloonExplode()
    }

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

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    val viewModel: HomeViewModel = viewModel()

    MulkkamTheme {
        HomeScreen(
            viewModel = viewModel,
            navigateToNotification = {},
            onManualDrink = {},
        )
    }
}
