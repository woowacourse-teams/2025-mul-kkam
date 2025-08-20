package com.mulkkam.ui.main

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import com.mulkkam.R
import com.mulkkam.databinding.ActivityMainBinding
import com.mulkkam.ui.custom.snackbar.CustomSnackBar
import com.mulkkam.ui.main.dialog.MainPermissionDialogFragment
import com.mulkkam.ui.main.model.MainTab
import com.mulkkam.ui.service.NotificationAction
import com.mulkkam.ui.service.NotificationService
import com.mulkkam.ui.util.binding.BindingActivity
import com.mulkkam.ui.util.extensions.isHealthConnectAvailable
import com.mulkkam.ui.widget.IntakeWidget
import com.mulkkam.ui.widget.ProgressWidget

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
        initDoubleBackToExit()
        initObservers()
        loadDeviceId()

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

    private fun initDoubleBackToExit() {
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
        with(viewModel) {
            isHealthPermissionGranted.observe(this@MainActivity) { isGranted ->
                if (isGranted) {
                    viewModel.scheduleCalorieCheck()
                }
            }

            fcmToken.observe(this@MainActivity) { token ->
                token?.let {
                    viewModel.saveDeviceInfo(loadDeviceId())
                }
            }

            onFirstLaunch.observe(this@MainActivity) {
                showMainPermissionDialog()
            }
        }
    }

    private fun showMainPermissionDialog() {
        if (supportFragmentManager.findFragmentByTag(MainPermissionDialogFragment.TAG) != null) return
        MainPermissionDialogFragment
            .newInstance()
            .show(supportFragmentManager, MainPermissionDialogFragment.TAG)
    }

    @SuppressLint("HardwareIds")
    private fun loadDeviceId(): String =
        Settings.Secure.getString(
            this.contentResolver,
            Settings.Secure.ANDROID_ID,
        )

    override fun onStop() {
        super.onStop()
        IntakeWidget.refresh(this)
        updateProgressWidgets()
    }

    private fun updateProgressWidgets() {
        val appWidgetManager = AppWidgetManager.getInstance(this)
        val progressWidget = ComponentName(this, ProgressWidget::class.java)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(progressWidget)
        appWidgetIds.forEach { appWidgetId ->
            ProgressWidget.updateProgressWidget(this, appWidgetId)
        }
    }

    companion object {
        const val SNACK_BAR_BOTTOM_NAV_OFFSET: Float = -94f
        const val TOAST_BOTTOM_NAV_OFFSET: Float = 94f

        private const val BACK_PRESS_THRESHOLD: Long = 2000L

        val HEALTH_CONNECT_PERMISSIONS =
            setOf(
                HealthPermission.getReadPermission(ActiveCaloriesBurnedRecord::class),
                HealthPermission.PERMISSION_READ_HEALTH_DATA_IN_BACKGROUND,
            )

        fun newIntent(context: Context): Intent = Intent(context, MainActivity::class.java)

        fun newPendingIntent(context: Context): PendingIntent =
            PendingIntent.getActivity(context, 0, newIntent(context), PendingIntent.FLAG_IMMUTABLE)
    }
}
