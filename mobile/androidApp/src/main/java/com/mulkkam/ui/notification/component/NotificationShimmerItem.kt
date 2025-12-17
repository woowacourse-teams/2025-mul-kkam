package com.mulkkam.ui.notification.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp

@Composable
fun NotificationShimmerItem(brush: Brush) {
    Row(
        modifier =
            Modifier.padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Spacer(
            modifier =
                Modifier
                    .size(38.dp, 38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(brush),
        )
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start,
        ) {
            Spacer(
                modifier =
                    Modifier
                        .height(20.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .fillMaxWidth(fraction = 0.7f)
                        .background(brush),
            )
            Spacer(modifier = Modifier.height(4.dp))
            Spacer(
                modifier =
                    Modifier
                        .height(20.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .fillMaxWidth(fraction = 0.4f)
                        .background(brush),
            )
        }
    }
}
