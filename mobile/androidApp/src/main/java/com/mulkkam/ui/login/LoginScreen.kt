package com.mulkkam.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mulkkam.R
import com.mulkkam.ui.component.MulKkamSnackbarHost
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.White
import com.mulkkam.ui.login.component.KakaoLoginButton
import com.mulkkam.ui.splash.component.AppUpdateDialog

@Composable
fun LoginScreen(
    onLoginClick: () -> Unit,
    snackbarHostState: SnackbarHostState,
    isLoginLoading: Boolean,
    navigateToPlayStoreAndExit: () -> Unit,
    modifier: Modifier = Modifier,
    showDialog: Boolean = false,
) {
    Scaffold(
        modifier = modifier,
        containerColor = White,
        snackbarHost = { MulKkamSnackbarHost(hostState = snackbarHostState) },
    ) { innerPadding ->
        if (showDialog) {
            AppUpdateDialog(
                navigateToPlayStoreAndExit = navigateToPlayStoreAndExit,
            )
        }

        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_home_character),
                contentDescription = null,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 62.dp)
                        .aspectRatio(1f),
                contentScale = ContentScale.Fit,
            )
            Spacer(modifier = Modifier.height(64.dp))
            KakaoLoginButton(
                onClick = onLoginClick,
                isEnabled = isLoginLoading.not(),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    MulKkamTheme {
        LoginScreen(
            onLoginClick = {},
            snackbarHostState = SnackbarHostState(),
            isLoginLoading = false,
            navigateToPlayStoreAndExit = {},
        )
    }
}
