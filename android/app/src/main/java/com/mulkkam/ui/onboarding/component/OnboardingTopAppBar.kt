package com.mulkkam.ui.onboarding.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mulkkam.R
import com.mulkkam.ui.designsystem.Gray400
import com.mulkkam.ui.designsystem.MulkkamTheme

@Composable
fun OnboardingTopAppBar(
    onBackClick: () -> Unit,
    currentProgress: Int,
) {
    Column {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.size(48.dp),
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_common_prev),
                contentDescription = null,
                tint = Gray400,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        SegmentedProgressBar(
            currentProgress = currentProgress,
            modifier = Modifier.fillMaxWidth().height(2.dp).padding(horizontal = 24.dp),
        )

        Spacer(modifier = Modifier.height(10.dp))
    }
}

@Preview(showBackground = true)
@Composable
private fun OnboardingTopAppBarPreview() {
    MulkkamTheme {
        OnboardingTopAppBar(
            onBackClick = {},
            currentProgress = 1,
        )
    }
}
