package com.mulkkam.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mulkkam.ui.designsystem.Gray200
import com.mulkkam.ui.designsystem.GrayAlert
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.White
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import mulkkam.shared.generated.resources.Res
import mulkkam.shared.generated.resources.ic_info_circle
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

private val DEFAULT_SNACKBAR_SHAPE: RoundedCornerShape = RoundedCornerShape(size = 4.dp)

@Composable
fun MulKkamSnackbarHost(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(horizontal = 24.dp),
) {
    SnackbarHost(
        hostState = hostState,
        modifier =
            modifier
                .navigationBarsPadding()
                .fillMaxWidth()
                .padding(contentPadding),
    ) { snackbarData: SnackbarData ->
        val snackbarVisuals: SnackbarVisuals = snackbarData.visuals
        if (snackbarVisuals is MulKkamSnackbarVisuals) {
            val actionLabel: String? = snackbarVisuals.actionLabel
            val iconResource: DrawableResource = snackbarVisuals.iconResource
            val message: String = snackbarVisuals.message

            val contentModifier: Modifier = Modifier.fillMaxWidth()

            if (actionLabel.isNullOrBlank()) {
                MulKkamSnackbar(
                    message = message,
                    iconResource = iconResource,
                    modifier = contentModifier,
                )
            } else {
                MulKkamActionSnackbar(
                    message = message,
                    iconResource = iconResource,
                    actionLabel = actionLabel,
                    onActionClick = { snackbarData.performAction() },
                    modifier = contentModifier,
                )
            }
        } else {
            Snackbar(snackbarData = snackbarData)
        }
    }
}

@Composable
private fun MulKkamSnackbar(
    message: String,
    iconResource: DrawableResource,
    modifier: Modifier = Modifier,
) {
    MulKkamSnackbarContainer(modifier = modifier) {
        MulKkamSnackbarLeadingIcon(iconResource = iconResource)
        Text(
            text = message,
            style = MulKkamTheme.typography.body2,
            color = White,
            modifier = Modifier.weight(weight = 1f),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun MulKkamActionSnackbar(
    message: String,
    iconResource: DrawableResource,
    actionLabel: String,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val actionClick: () -> Unit = rememberUpdatedState(newValue = onActionClick).value
    MulKkamSnackbarContainer(modifier = modifier) {
        MulKkamSnackbarLeadingIcon(iconResource = iconResource)
        Text(
            text = message,
            style = MulKkamTheme.typography.body2,
            color = White,
            modifier =
                Modifier
                    .weight(weight = 1f)
                    .padding(end = 10.dp),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        MulKkamSnackbarAction(
            actionLabel = actionLabel,
            onActionClick = actionClick,
        )
    }
}

@Composable
private fun MulKkamSnackbarContainer(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    Box(
        modifier =
            modifier
                .padding(vertical = 8.dp)
                .background(color = GrayAlert, shape = DEFAULT_SNACKBAR_SHAPE)
                .padding(horizontal = 16.dp, vertical = 10.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(space = 10.dp),
        ) {
            content()
        }
    }
}

@Composable
private fun MulKkamSnackbarLeadingIcon(iconResource: DrawableResource) {
    Image(
        painter = painterResource(resource = iconResource),
        contentDescription = null,
        modifier = Modifier.size(32.dp),
    )
}

@Composable
private fun MulKkamSnackbarAction(
    actionLabel: String,
    onActionClick: () -> Unit,
) {
    val currentOnActionClick: () -> Unit = rememberUpdatedState(newValue = onActionClick).value
    Box(
        modifier =
            Modifier
                .border(width = 1.dp, color = Gray200, shape = DEFAULT_SNACKBAR_SHAPE)
                .background(color = GrayAlert, shape = DEFAULT_SNACKBAR_SHAPE)
                .clickable(
                    onClick = currentOnActionClick,
                    onClickLabel = actionLabel,
                ).padding(horizontal = 8.dp, vertical = 6.dp),
    ) {
        Text(
            text = actionLabel,
            style = MulKkamTheme.typography.body4,
            color = White,
        )
    }
}

private data class MulKkamSnackbarVisuals(
    override val message: String,
    val iconResource: DrawableResource,
    override val actionLabel: String?,
    override val duration: SnackbarDuration,
    override val withDismissAction: Boolean = false,
) : SnackbarVisuals

suspend fun SnackbarHostState.showMulKkamSnackbar(
    message: String,
    iconResource: DrawableResource,
    duration: SnackbarDuration = SnackbarDuration.Short,
): SnackbarResult {
    currentSnackbarData?.dismiss()
    val visuals =
        MulKkamSnackbarVisuals(
            message = message,
            iconResource = iconResource,
            actionLabel = null,
            duration = duration,
        )
    return showSnackbar(visuals = visuals)
}

suspend fun SnackbarHostState.showMulKkamActionSnackbar(
    message: String,
    iconResource: DrawableResource,
    actionLabel: String,
    duration: SnackbarDuration = SnackbarDuration.Short,
    onActionPerformed: () -> Unit = {},
): SnackbarResult {
    currentSnackbarData?.dismiss()
    val visuals =
        MulKkamSnackbarVisuals(
            message = message,
            iconResource = iconResource,
            actionLabel = actionLabel,
            duration = duration,
        )
    val result: SnackbarResult = showSnackbar(visuals = visuals)
    if (result == SnackbarResult.ActionPerformed) {
        onActionPerformed()
    }
    return result
}

@Preview(showBackground = true)
@Composable
private fun MulKkamSnackbarPreview() {
    MulKkamTheme {
        MulKkamSnackbar(
            message = "돈먹환돈먹환돈먹환 안녕하세요",
            iconResource = Res.drawable.ic_info_circle,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MulKkamActionSnackbarPreview() {
    MulKkamTheme {
        MulKkamActionSnackbar(
            message = "환노는 돈까스를 먹으러",
            iconResource = Res.drawable.ic_info_circle,
            actionLabel = "바로감",
            onActionClick = {},
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
