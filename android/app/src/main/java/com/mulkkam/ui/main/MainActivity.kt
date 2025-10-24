package com.mulkkam.ui.main

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
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
import com.mulkkam.ui.notification.NotificationActivity
import com.mulkkam.ui.service.NotificationAction
import com.mulkkam.ui.service.NotificationService
import com.mulkkam.ui.splash.dialog.AppUpdateDialogFragment
import com.mulkkam.ui.util.binding.BindingActivity
import com.mulkkam.ui.util.extensions.getAppVersion
import com.mulkkam.ui.util.extensions.isHealthConnectAvailable
import com.mulkkam.ui.widget.AchievementHeatmapWidget
import com.mulkkam.ui.widget.IntakeWidget
import com.mulkkam.ui.widget.ProgressWidget
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BindingActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {
    override val needBottomPadding: Boolean
        get() = binding.bnvMain.isVisible.not()

    private val viewModel: MainViewModel by viewModels()

    private var backPressedTime: Long = 0L

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleNotificationEvent()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.checkAppVersion(getAppVersion())
        initBottomNavListener()
        if (savedInstanceState == null) {
            switchFragment(MainTab.HOME)
        }
        initDoubleBackToExit()
        initObservers()
        handleNotificationEvent()

        if (isHealthConnectAvailable()) {
            viewModel.checkHealthPermissions(setOf(PERMISSION_ACTIVE_CALORIES_BURNED, PERMISSION_HEALTH_DATA_IN_BACKGROUND))
        }
    }

    private fun handleNotificationEvent() {
        intent?.let {
            val action =
                NotificationAction.from(it.getStringExtra(NotificationService.EXTRA_ACTION))
            when (action) {
                NotificationAction.GO_HOME -> Unit
                NotificationAction.GO_NOTIFICATION -> {
                    val intent = NotificationActivity.newIntent(this)
                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                }

                NotificationAction.FRIEND_REMINDER -> {
                    if (binding.bnvMain.selectedItemId != R.id.item_home) {
                        switchFragment(MainTab.HOME)
                        binding.bnvMain.selectedItemId = R.id.item_home
                    }
                    viewModel.receiveFriendWaterBalloon()
                }

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

            fcmToken.observe(this@MainActivity) {
                val currentGranted = hasNotificationPermission()
                viewModel.saveNotificationPermission(
                    isCurrentlyGranted = currentGranted,
                )
            }

            onFirstLaunch.observe(this@MainActivity) {
                showMainPermissionDialog()
            }

            isAppOutdated.observe(this@MainActivity) { isAppOutdated ->
                if (isAppOutdated) showUpdateDialog()
            }
        }
    }

    private fun showUpdateDialog() {
        if (supportFragmentManager.findFragmentByTag(AppUpdateDialogFragment.TAG) != null) return
        AppUpdateDialogFragment
            .newInstance()
            .show(supportFragmentManager, AppUpdateDialogFragment.TAG)
    }

    private fun showMainPermissionDialog() {
        if (supportFragmentManager.findFragmentByTag(MainPermissionDialogFragment.TAG) != null) return
        MainPermissionDialogFragment
            .newInstance()
            .show(supportFragmentManager, MainPermissionDialogFragment.TAG)
    }

    private fun hasNotificationPermission(): Boolean =
        when {
            Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU -> true
            else ->
                ContextCompat.checkSelfPermission(
                    this,
                    POST_NOTIFICATIONS,
                ) == PackageManager.PERMISSION_GRANTED
        }

    override fun onStop() {
        super.onStop()
        IntakeWidget.refresh(this)
        ProgressWidget.refresh(this)
        AchievementHeatmapWidget.refresh(this)
    }

    companion object {
        const val SNACK_BAR_BOTTOM_NAV_OFFSET: Float = -94f
        const val TOAST_BOTTOM_NAV_OFFSET: Float = 94f

        private const val BACK_PRESS_THRESHOLD: Long = 2000L

        const val PERMISSION_HEALTH_DATA_IN_BACKGROUND: String =
            HealthPermission.PERMISSION_READ_HEALTH_DATA_IN_BACKGROUND
        val PERMISSION_ACTIVE_CALORIES_BURNED: String =
            HealthPermission.getReadPermission(ActiveCaloriesBurnedRecord::class)

        fun newIntent(context: Context): Intent = Intent(context, MainActivity::class.java)

        fun newPendingIntent(context: Context): PendingIntent =
            PendingIntent.getActivity(context, 0, newIntent(context), PendingIntent.FLAG_IMMUTABLE)
    }
}
