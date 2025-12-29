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

private const val DEFAULT_TOAST_DURATION_MILLIS: Long = 2000L
private val DEFAULT_TOAST_SHAPE: RoundedCornerShape = RoundedCornerShape(size = 4.dp)

data class MulKkamToastVisuals(
    val message: String,
    @param:DrawableRes val iconResourceId: Int,
)

class MulKkamToastState {
    private val mutex: Mutex = Mutex()
    private val currentToastFlow: MutableStateFlow<MulKkamToastVisuals?> = MutableStateFlow(value = null)

    val currentToast: StateFlow<MulKkamToastVisuals?> = currentToastFlow.asStateFlow()

    suspend fun showMulKkamToast(
        message: String,
        @DrawableRes iconResourceId: Int,
        durationMillis: Long = DEFAULT_TOAST_DURATION_MILLIS,
    ) {
        mutex.withLock {
            currentToastFlow.emit(
                MulKkamToastVisuals(
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
fun rememberMulKkamToastState(): MulKkamToastState {
    val toastState: MulKkamToastState = remember { MulKkamToastState() }
    return toastState
}

@Composable
fun MulKkamToastHost(
    state: MulKkamToastState,
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
    val toastState: State<MulKkamToastVisuals?> = state.currentToast.collectAsState()
    val toastVisuals: MulKkamToastVisuals? = toastState.value

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = alignment,
    ) {
        AnimatedVisibility(
            visible = toastVisuals != null,
            enter = fadeIn() + slideInVertically { fullHeight: Int -> fullHeight / 4 },
            exit = fadeOut() + slideOutVertically { fullHeight: Int -> fullHeight / 4 },
        ) {
            val currentVisuals: MulKkamToastVisuals = toastVisuals ?: return@AnimatedVisibility
            MulKkamToast(
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
private fun MulKkamToast(
    message: String,
    @DrawableRes iconResourceId: Int,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .background(color = GrayAlert, shape = DEFAULT_TOAST_SHAPE)
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
