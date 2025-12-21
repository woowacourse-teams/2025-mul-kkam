package com.mulkkam.ui.login.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mulkkam.R
import com.mulkkam.ui.designsystem.Black
import com.mulkkam.ui.designsystem.KakaoYellow
import com.mulkkam.ui.designsystem.MulKkamTheme

@Composable
fun KakaoLoginButton(
    onClick: () -> Unit,
    isEnabled: Boolean,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .height(52.dp),
        shape = RoundedCornerShape(8.dp),
        color = KakaoYellow,
        contentColor = Black,
        onClick = onClick,
        enabled = isEnabled,
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.ic_login_kakao),
                contentDescription = stringResource(R.string.login_kakao),
                modifier =
                    Modifier
                        .padding(start = 30.dp)
                        .size(16.dp)
                        .align(Alignment.CenterStart),
            )
            Text(
                text = stringResource(R.string.login_kakao),
                modifier =
                    Modifier
                        .padding(start = 16.dp)
                        .align(Alignment.Center),
                style = MulKkamTheme.typography.title2,
                color = Black,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun KakaoLoginButtonPreview() {
    MulKkamTheme {
        KakaoLoginButton(
            onClick = {},
            isEnabled = true,
        )
    }
}
