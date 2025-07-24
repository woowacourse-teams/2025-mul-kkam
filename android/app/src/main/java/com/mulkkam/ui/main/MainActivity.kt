package com.mulkkam.ui.main

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.mulkkam.R
import com.mulkkam.databinding.ActivityMainBinding
import com.mulkkam.ui.binding.BindingActivity
import com.mulkkam.ui.model.MainTab

class MainActivity : BindingActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {
    override val needBottomPadding: Boolean
        get() = binding.bnvMain.isVisible.not()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initBottomNavListener()
        if (savedInstanceState == null) {
            switchFragment(MainTab.HOME)
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
}
