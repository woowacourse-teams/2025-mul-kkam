package com.mulkkam.ui.auth.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mulkkam.ui.auth.login.component.KakaoLoginButton
import com.mulkkam.ui.auth.login.model.AuthPlatform
import com.mulkkam.ui.auth.splash.component.AppUpdateDialog
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.White
import mulkkam.shared.generated.resources.Res
import mulkkam.shared.generated.resources.img_home_character
import org.jetbrains.compose.resources.painterResource

@Composable
actual fun LoginScreen(
    padding: PaddingValues,
    onLoginClick: (authPlatform: AuthPlatform) -> Unit,
    isLoginLoading: Boolean,
    snackbarHostState: SnackbarHostState,
    navigateToStore: () -> Unit,
    showDialog: Boolean,
) {
    Scaffold(
        containerColor = White,
        modifier = Modifier.padding(padding),
    ) { innerPadding ->
        if (showDialog) {
            AppUpdateDialog(
                navigateToStore = navigateToStore,
            )
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Image(
                painter = painterResource(resource = Res.drawable.img_home_character),
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
                onClick = { onLoginClick(AuthPlatform.KAKAO) },
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
            padding = PaddingValues(),
            onLoginClick = {},
            snackbarHostState = SnackbarHostState(),
            isLoginLoading = false,
            navigateToStore = {},
        )
    }
}
