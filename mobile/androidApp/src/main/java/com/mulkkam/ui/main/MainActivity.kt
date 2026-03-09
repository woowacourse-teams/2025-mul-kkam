package com.mulkkam.ui.main

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.health.connect.client.PermissionController
import com.google.firebase.messaging.FirebaseMessaging
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.mulkkam.MulKkamApp
import com.mulkkam.R
import com.mulkkam.ui.auth.login.model.AuthPlatform
import com.mulkkam.ui.custom.toast.CustomToast
import com.mulkkam.ui.util.extensions.getAppVersion
import com.mulkkam.ui.util.extensions.isHealthConnectAvailable
import org.koin.androidx.viewmodel.ext.android.viewModel

@SuppressLint("CustomSplashScreen")
class MainActivity : FragmentActivity() {
    private val mainViewModel: MainViewModel by viewModel()
    private var onPushTokenUpdated: ((token: String) -> Unit)? = null
    private var onPushPermissionUpdated: ((granted: Boolean) -> Unit)? = null
    private var onPushRegistrationError: ((errorMessage: String) -> Unit)? = null

    private val requestHealthConnectLauncher =
        registerForActivityResult(PermissionController.createRequestPermissionResultContract()) { results ->
            handleHealthPermissionResult(results.contains(MainActivity2.PERMISSION_HEALTH_DATA_IN_BACKGROUND))
            requestNotificationPermission()
        }

    private val requestNotificationLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            handleNotificationPermissionResult(granted)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MulKkamApp(
                onLogin = ::login,
                onRegisterPushNotification = ::registerPushNotification,
                onRequestInitialPermissions = ::requestHealthPermissions,
                appVersion = this.getAppVersion(),
            )
        }
    }

    private fun login(
        authPlatform: AuthPlatform,
        onSuccess: (token: String) -> Unit,
        onError: (errorMessage: String) -> Unit,
    ) {
        when (authPlatform) {
            AuthPlatform.KAKAO -> {
                loginWithKakao(onSuccess, onError)
            }

            else -> {
                Unit
            }
        }
    }

    private fun loginWithKakao(
        onSuccess: (token: String) -> Unit,
        onError: (errorMessage: String) -> Unit,
    ) {
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
            loginWithKakaoTalk(onSuccess, onError)
        } else {
            loginWithKakaoAccount(onSuccess, onError)
        }
    }

    private fun loginWithKakaoTalk(
        onSuccess: (token: String) -> Unit,
        onError: (errorMessage: String) -> Unit,
    ) {
        UserApiClient.instance.loginWithKakaoTalk(this) { token, error ->
            when {
                error is ClientError && error.reason == ClientErrorCause.Cancelled -> {
                    Unit
                }

                error != null -> {
                    loginWithKakaoAccount(onSuccess, onError)
                }

                else -> {
                    token?.let {
                        onSuccess(it.accessToken)
                    }
                }
            }
        }
    }

    private fun loginWithKakaoAccount(
        onSuccess: (token: String) -> Unit,
        onError: (errorMessage: String) -> Unit,
    ) {
        UserApiClient.instance.loginWithKakaoAccount(this) { token, error ->
            if (error != null) {
                onError(error.message ?: "Login Error")
            } else {
                token?.let {
                    onSuccess(it.accessToken)
                }
            }
        }
    }

    private fun registerPushNotification(
        onTokenUpdated: (token: String) -> Unit,
        onPermissionUpdated: (granted: Boolean) -> Unit,
        onError: (errorMessage: String) -> Unit,
    ) {
        onPushTokenUpdated = onTokenUpdated
        onPushPermissionUpdated = onPermissionUpdated
        onPushRegistrationError = onError

        val isPermissionGranted: Boolean = isNotificationPermissionGranted()
        notifyPushPermissionState()
        if (isPermissionGranted) {
            requestFirebaseMessagingToken()
        }
    }

    private fun isNotificationPermissionGranted(): Boolean =
        when {
            Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU -> {
                true
            }

            else -> {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS,
                ) == PackageManager.PERMISSION_GRANTED
            }
        }

    private fun requestHealthPermissions() {
        if (isHealthConnectAvailable()) {
            requestHealthConnectLauncher.launch(
                setOf(
                    MainActivity2.PERMISSION_ACTIVE_CALORIES_BURNED,
                    MainActivity2.PERMISSION_HEALTH_DATA_IN_BACKGROUND,
                ),
            )
        } else {
            CustomToast
                .makeText(this, getString(R.string.health_connect_install_alert), R.drawable.ic_info_circle)
                .apply {
                    setGravityY(MainActivity2.TOAST_BOTTOM_NAV_OFFSET)
                }.show()
            requestNotificationPermission()
        }
    }

    private fun handleHealthPermissionResult(isGranted: Boolean) {
        val messageResId =
            if (isGranted) {
                mainViewModel.scheduleCalorieCheck()
                R.string.main_health_permission_granted
            } else {
                R.string.main_health_permission_denied
            }

        CustomToast
            .makeText(this, getString(messageResId), R.drawable.ic_info_circle)
            .apply {
                setGravityY(TOAST_BOTTOM_NAV_OFFSET)
            }.show()
    }

    private fun handleNotificationPermissionResult(isGranted: Boolean) {
        onPushPermissionUpdated?.invoke(isGranted)
        if (isGranted) {
            requestFirebaseMessagingToken()
        }

        val messageResId =
            if (isGranted) {
                R.string.main_alarm_permission_granted
            } else {
                R.string.main_alarm_permission_denied
            }

        CustomToast.Companion
            .makeText(this, getString(messageResId), R.drawable.ic_info_circle)
            .apply {
                setGravityY(TOAST_BOTTOM_NAV_OFFSET)
            }.show()
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU || isNotificationPermissionGranted()) {
            notifyPushPermissionState()
            return
        }

        requestNotificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    private fun requestFirebaseMessagingToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                onPushRegistrationError?.invoke(task.exception?.message ?: "Failed to fetch notification token.")
                return@addOnCompleteListener
            }

            val token = task.result ?: return@addOnCompleteListener
            onPushTokenUpdated?.invoke(token)
        }
    }

    private fun notifyPushPermissionState() {
        onPushPermissionUpdated?.invoke(isNotificationPermissionGranted())
    }

    companion object {
        const val TOAST_BOTTOM_NAV_OFFSET: Float = 94f

        fun newIntent(context: Context): Intent =
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }

        fun newPendingIntent(context: Context): PendingIntent =
            PendingIntent.getActivity(context, 0, newIntent(context), PendingIntent.FLAG_IMMUTABLE)
    }
}
