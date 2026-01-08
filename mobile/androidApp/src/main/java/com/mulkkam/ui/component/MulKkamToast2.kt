package com.mulkkam.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mulkkam.ui.designsystem.GrayAlert
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.White
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

private const val DEFAULT_TOAST_DURATION_MILLIS2: Long = 2000L
private val DEFAULT_TOAST_SHAPE2: RoundedCornerShape = RoundedCornerShape(size = 4.dp)

data class MulKkamToastVisuals2(
    val message: String,
    @param:DrawableRes val iconResourceId: Int,
)

class MulKkamToastState2 {
    private val mutex: Mutex = Mutex()
    private val currentToastFlow: MutableStateFlow<MulKkamToastVisuals2?> = MutableStateFlow(value = null)

    val currentToast: StateFlow<MulKkamToastVisuals2?> = currentToastFlow.asStateFlow()

    suspend fun showMulKkamToast2(
        message: String,
        @DrawableRes iconResourceId: Int,
        durationMillis: Long = DEFAULT_TOAST_DURATION_MILLIS2,
    ) {
        mutex.withLock {
            currentToastFlow.emit(
                MulKkamToastVisuals2(
                    message = message,
                    iconResourceId = iconResourceId,
                ),
            )
            delay(timeMillis = durationMillis)
            currentToastFlow.emit(value = null)
        }
    }
}

@Composable
fun rememberMulKkamToastState2(): MulKkamToastState2 {
    val toastState: MulKkamToastState2 = remember { MulKkamToastState2() }
    return toastState
}

@Composable
fun MulKkamToastHost2(
    state: MulKkamToastState2,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.BottomCenter,
    contentPadding: PaddingValues =
        PaddingValues(
            start = 24.dp,
            top = 0.dp,
            end = 24.dp,
            bottom = 24.dp,
        ),
) {
    val toastState: State<MulKkamToastVisuals2?> = state.currentToast.collectAsState()
    val toastVisuals: MulKkamToastVisuals2? = toastState.value

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = alignment,
    ) {
        AnimatedVisibility(
            visible = toastVisuals != null,
            enter = fadeIn() + slideInVertically { fullHeight: Int -> fullHeight / 4 },
            exit = fadeOut() + slideOutVertically { fullHeight: Int -> fullHeight / 4 },
        ) {
            val currentVisuals: MulKkamToastVisuals2 = toastVisuals ?: return@AnimatedVisibility
            MulKkamToast2(
                message = currentVisuals.message,
                iconResourceId = currentVisuals.iconResourceId,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(contentPadding),
            )
        }
    }
}

@Composable
private fun MulKkamToast2(
    message: String,
    @DrawableRes iconResourceId: Int,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .background(color = GrayAlert, shape = DEFAULT_TOAST_SHAPE2)
                .padding(horizontal = 16.dp, vertical = 10.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(space = 10.dp),
        ) {
            Image(
                painter = painterResource(id = iconResourceId),
                contentDescription = null,
                modifier = Modifier.size(32.dp),
            )
            Text(
                text = message,
                style = MulKkamTheme.typography.body2,
                color = White,
                modifier = Modifier.weight(weight = 1f),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
