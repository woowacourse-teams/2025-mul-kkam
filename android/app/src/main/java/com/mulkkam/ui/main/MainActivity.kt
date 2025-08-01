package com.mulkkam.ui.main

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.google.android.material.snackbar.Snackbar
import com.mulkkam.R
import com.mulkkam.databinding.ActivityMainBinding
import com.mulkkam.ui.binding.BindingActivity
import com.mulkkam.ui.model.MainTab
import com.mulkkam.ui.service.NotificationAction
import com.mulkkam.ui.service.NotificationService

class MainActivity : BindingActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {
    override val needBottomPadding: Boolean
        get() = binding.bnvMain.isVisible.not()

    private var backPressedTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initBottomNavListener()
        if (savedInstanceState == null) {
            switchFragment(MainTab.HOME)
        }
        handleNotificationEvent()
        setupDoubleBackToExit()
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

    companion object {
        private const val BACK_PRESS_THRESHOLD: Long = 2000L
    }
}
