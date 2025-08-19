package com.mulkkam.ui.main

import android.Manifest.permission.POST_NOTIFICATIONS
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import com.mulkkam.R
import com.mulkkam.databinding.ActivityMainBinding
import com.mulkkam.ui.custom.snackbar.CustomSnackBar
import com.mulkkam.ui.main.model.MainTab
import com.mulkkam.ui.service.NotificationAction
import com.mulkkam.ui.service.NotificationService
import com.mulkkam.ui.util.binding.BindingActivity
import com.mulkkam.ui.util.extensions.isHealthConnectAvailable

class MainActivity : BindingActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {
    override val needBottomPadding: Boolean
        get() = binding.bnvMain.isVisible.not()

    private val viewModel: MainViewModel by viewModels()

    private var backPressedTime: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initBottomNavListener()
        if (savedInstanceState == null) {
            switchFragment(MainTab.HOME)
        }
        handleNotificationEvent()
        setupDoubleBackToExit()
        initObservers()
        loadDeviceId()
        requestNotificationPermission()

        if (isHealthConnectAvailable()) {
            viewModel.checkHealthPermissions(HEALTH_CONNECT_PERMISSIONS)
        }
    }

    private fun handleNotificationEvent() {
        intent?.let {
            val action =
                NotificationAction.from(it.getStringExtra(NotificationService.EXTRA_ACTION))

            // TODO: 푸시 알림 클릭 시 처리 로직 추가
            when (action) {
                NotificationAction.GO_HOME -> Unit
                NotificationAction.GO_NOTIFICATION -> Unit
                NotificationAction.UNKNOWN -> Unit
            }
        }
    }

    private fun initBottomNavListener() {
        binding.bnvMain.setOnItemSelectedListener { item ->
            val menu = MainTab.from(item.itemId) ?: return@setOnItemSelectedListener false
            switchFragment(menu)
            true
        }
    }

    private fun switchFragment(targetTab: MainTab) {
        val targetFragment = prepareFragment(targetTab)

        showOnlyFragment(targetFragment)
    }

    private fun prepareFragment(targetTab: MainTab): Fragment {
        val tag = targetTab.name
        return supportFragmentManager.findFragmentByTag(tag)
            ?: targetTab.create().also { fragment ->
                supportFragmentManager.commit {
                    setReorderingAllowed(true)
                    add(R.id.fcv_main, fragment, tag)
                }
            }
    }

    private fun showOnlyFragment(targetFragment: Fragment) {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            supportFragmentManager.fragments.forEach { fragment ->
                if (fragment == targetFragment) {
                    show(fragment)
                    notifyFragmentReselected(fragment)
                } else {
                    hide(fragment)
                }
            }
        }
    }

    private fun notifyFragmentReselected(fragment: Fragment) {
        if (fragment is Refreshable) {
            fragment.onReselected()
        }
    }

    private fun setupDoubleBackToExit() {
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (System.currentTimeMillis() - backPressedTime >= BACK_PRESS_THRESHOLD) {
                        backPressedTime = System.currentTimeMillis()
                        CustomSnackBar
                            .make(
                                binding.root,
                                getString(R.string.main_main_back_press_exit_message),
                                R.drawable.ic_info_circle,
                            ).apply {
                                setTranslationY(SNACK_BAR_BOTTOM_NAV_OFFSET)
                            }.show()
                    } else {
                        finishAffinity()
                    }
                }
            },
        )
    }

    private fun initObservers() {
        viewModel.isHealthPermissionGranted.observe(this) { isGranted ->
            if (isGranted) {
                viewModel.scheduleCalorieCheck()
            }
        }

        viewModel.fcmToken.observe(this) { token ->
            token?.let {
                viewModel.saveDeviceInfo(loadDeviceId())
            }
        }
    }

    @SuppressLint("HardwareIds")
    private fun loadDeviceId(): String =
        Settings.Secure.getString(
            this.contentResolver,
            Settings.Secure.ANDROID_ID,
        )

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        if (hasNotificationPermission()) return

        val shouldShowRationale =
            ActivityCompat.shouldShowRequestPermissionRationale(this, POST_NOTIFICATIONS)
        if (!shouldShowRationale) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(POST_NOTIFICATIONS),
                REQUEST_CODE_NOTIFICATION_PERMISSION,
            )
        }
    }

    private fun hasNotificationPermission(): Boolean =
        when {
            Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU -> true
            else -> checkSelfPermission(POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            REQUEST_CODE_NOTIFICATION_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    CustomSnackBar
                        .make(
                            binding.root,
                            getString(R.string.main_alarm_permission_granted),
                            R.drawable.ic_info_circle,
                        ).apply { setTranslationY(SNACK_BAR_BOTTOM_NAV_OFFSET) }
                        .show()
                } else {
                    CustomSnackBar
                        .make(
                            binding.root,
                            getString(R.string.main_alarm_permission_denied),
                            R.drawable.ic_info_circle,
                        ).apply { setTranslationY(SNACK_BAR_BOTTOM_NAV_OFFSET) }
                        .show()
                }
            }
        }
    }

    companion object {
        const val SNACK_BAR_BOTTOM_NAV_OFFSET: Float = -94f
        const val TOAST_BOTTOM_NAV_OFFSET: Float = 94f
        private const val REQUEST_CODE_NOTIFICATION_PERMISSION: Int = 1001
        private const val BACK_PRESS_THRESHOLD: Long = 2000L
        private val HEALTH_CONNECT_PERMISSIONS =
            setOf(
                HealthPermission.getReadPermission(ActiveCaloriesBurnedRecord::class),
                HealthPermission.PERMISSION_READ_HEALTH_DATA_IN_BACKGROUND,
            )

        fun newIntent(context: Context): Intent = Intent(context, MainActivity::class.java)
    }
}
