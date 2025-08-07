package com.mulkkam.ui.main

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import com.google.android.material.snackbar.Snackbar
import com.mulkkam.R
import com.mulkkam.databinding.ActivityMainBinding
import com.mulkkam.ui.binding.BindingActivity
import com.mulkkam.ui.model.MainTab
import com.mulkkam.ui.service.NotificationAction
import com.mulkkam.ui.service.NotificationService
import com.mulkkam.util.extensions.isHealthConnectAvailable

class MainActivity : BindingActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {
    override val needBottomPadding: Boolean
        get() = binding.bnvMain.isVisible.not()

    private val viewModel: MainViewModel by viewModels()

    // TODO: 온보딩으로 로직 이동
    private val requestPermissionsLauncher =
        registerForActivityResult(PermissionController.createRequestPermissionResultContract()) { results ->
            if (results.containsAll(HEALTH_CONNECT_PERMISSIONS)) {
                viewModel.updateHealthPermissionStatus(true)
            }
        }

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

        if (isHealthConnectAvailable()) {
            viewModel.checkHealthPermissions(HEALTH_CONNECT_PERMISSIONS)
        }
    }

    private fun handleNotificationEvent() {
        intent?.let {
            val action = NotificationAction.from(it.getStringExtra(NotificationService.EXTRA_ACTION))

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
                        Snackbar.make(binding.root, R.string.main_main_back_press_exit_message, Snackbar.LENGTH_SHORT).show()
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
            } else {
                requestPermissionsLauncher.launch(HEALTH_CONNECT_PERMISSIONS)
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

    companion object {
        private const val BACK_PRESS_THRESHOLD: Long = 2000L
        private val HEALTH_CONNECT_PERMISSIONS =
            setOf(
                HealthPermission.getReadPermission(ActiveCaloriesBurnedRecord::class),
            )

        fun newIntent(context: Context): Intent = Intent(context, MainActivity::class.java)
    }
}
