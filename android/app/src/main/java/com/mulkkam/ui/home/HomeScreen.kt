package com.mulkkam.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mulkkam.ui.designsystem.White
import com.mulkkam.ui.home.component.DrinkButton
import com.mulkkam.ui.home.component.HomeCharacter
import com.mulkkam.ui.home.component.HomeConfetti
import com.mulkkam.ui.home.component.HomeProgressOverview
import com.mulkkam.ui.home.component.HomeTopBar
import com.mulkkam.ui.home.component.rememberHomeUiStateHolder
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.model.MulKkamUiState.Loading.toSuccessDataOrNull
import kotlinx.coroutines.flow.collectLatest

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    navigateToNotification: () -> Unit,
    onManualDrink: () -> Unit,
) {
    val todayProgressUiState by viewModel.todayProgressInfoUiState.collectAsStateWithLifecycle()
    val cupsUiState by viewModel.cupsUiState.collectAsStateWithLifecycle()
    val alarmCountUiState by viewModel.alarmCountUiState.collectAsStateWithLifecycle()
    val drinkUiState by viewModel.drinkUiState.collectAsStateWithLifecycle()

    val uiStateHolder = rememberHomeUiStateHolder()

    LaunchedEffect(drinkUiState) {
        if (drinkUiState is MulKkamUiState.Success) {
            uiStateHolder.triggerDrinkAnimation()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.isGoalAchieved.collectLatest {
            uiStateHolder.triggerConfettiOnce()
        }
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
                    .padding(bottom = 62.dp)
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
    }
}
