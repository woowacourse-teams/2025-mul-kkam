package com.mulkkam.ui.history.history.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp

@Composable
fun HistoryShimmerScreen(brush: Brush) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(top = 28.dp),
    ) {
        Spacer(
            modifier =
                Modifier
                    .size(width = 120.dp, height = 28.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(brush),
        )

        Row(
            modifier =
                Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth()
                    .height(40.dp),
        ) {
            Spacer(
                modifier =
                    Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(brush),
            )
            Spacer(
                modifier =
                    Modifier
                        .padding(horizontal = 12.dp)
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(10.dp))
                        .background(brush),
            )
            Spacer(
                modifier =
                    Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(brush),
            )
        }

        Row(
            modifier =
                Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth()
                    .height(80.dp),
        ) {
            repeat(7) { index ->
                Spacer(
                    modifier =
                        Modifier
                            .padding(start = if (index == 0) 0.dp else 4.dp)
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(8.dp))
                            .background(brush),
                )
            }
        }

        Spacer(
            modifier =
                Modifier
                    .padding(top = 48.dp)
                    .size(width = 148.dp, height = 26.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(brush),
        )

        Spacer(
            modifier =
                Modifier
                    .padding(top = 32.dp)
                    .padding(horizontal = 64.dp)
                    .fillMaxWidth()
                    .height(232.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(brush),
        )

        Spacer(
            modifier =
                Modifier
                    .padding(top = 18.dp)
                    .size(width = 160.dp, height = 22.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(brush),
        )

        Spacer(
            modifier =
                Modifier
                    .padding(top = 38.dp)
                    .size(width = 60.dp, height = 22.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(brush),
        )

        repeat(4) { index ->
            Spacer(
                modifier =
                    Modifier
                        .padding(top = if (index == 0) 18.dp else 12.dp)
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(brush),
            )
        }
    }
}
