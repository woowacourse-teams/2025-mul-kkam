package com.mulkkam.ui.custom.numberpicker

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mulkkam.ui.designsystem.Black
import com.mulkkam.ui.designsystem.Gray300
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.MulkkamTheme
import com.mulkkam.ui.designsystem.Primary10
import com.mulkkam.ui.designsystem.White

@Composable
fun CustomNumberPicker(
    range: IntRange,
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    visibleCount: Int = 3,
    itemHeight: Dp = 36.dp,
) {
    val centerIndexOffset = (visibleCount - 1) / 2
    val listState =
        rememberLazyListState(
            initialFirstVisibleItemIndex = value - range.first,
        )

    LaunchedEffect(listState.isScrollInProgress) {
        if (!listState.isScrollInProgress) {
            val selectedIndex = listState.firstVisibleItemIndex
            onValueChange(range.first + selectedIndex)
        }
    }

    Box(
        modifier =
            modifier
                .height(itemHeight * visibleCount)
                .background(White),
        contentAlignment = Alignment.Center,
    ) {
        LazyColumn(
            state = listState,
            horizontalAlignment = Alignment.CenterHorizontally,
            flingBehavior = snapFlingBehavior(listState),
        ) {
            items(centerIndexOffset) {
                Spacer(modifier = Modifier.height(itemHeight))
            }

            items(range.count()) { index ->
                val number = range.first + index
                val isSelected = number == value

                Text(
                    text = number.toString(),
                    style = MulKkamTheme.typography.body2,
                    color = if (isSelected) Black else Gray300,
                    modifier =
                        Modifier
                            .height(itemHeight)
                            .fillMaxWidth()
                            .wrapContentHeight(Alignment.CenterVertically),
                    textAlign = TextAlign.Center,
                )
            }

            items(centerIndexOffset) {
                Spacer(modifier = Modifier.height(itemHeight))
            }
        }

        Box(
            modifier =
                Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
                    .height(itemHeight)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Primary10.copy(alpha = 0.3f)),
        )
    }
}

@Composable
fun snapFlingBehavior(listState: LazyListState): FlingBehavior =
    rememberSnapFlingBehavior(
        lazyListState = listState,
        snapPosition = SnapPosition.Center,
    )

@Preview(showBackground = true)
@Composable
private fun NumberPickerPreview() {
    MulkkamTheme {
        CustomNumberPicker(
            range = IntRange(1, 10),
            value = 6,
            onValueChange = {},
        )
    }
}
