package com.mulkkam.ui.main

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import com.mulkkam.R
import com.mulkkam.databinding.ActivityMainBinding
import com.mulkkam.ui.binding.BindingActivity
import com.mulkkam.ui.model.MainTab

class MainActivity : BindingActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {
    override val needBottomPadding: Boolean
        get() = binding.bnvMain.isVisible.not()

    private val tabs: MutableMap<MainTab, Fragment> = mutableMapOf()
    private var currentTab: MainTab? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initBottomNavListener()
        if (savedInstanceState == null) {
            switchFragment(MainTab.HOME)
        }
    }

    private fun initBottomNavListener() {
        binding.bnvMain.setOnItemSelectedListener { item ->
            MainTab.from(item.itemId)?.let { menu ->
                switchFragment(menu)
                true
            } ?: false
        }
    }

    private fun switchFragment(target: MainTab) {
        if (currentTab == target) return

        val fragment = prepareFragment(target)

        supportFragmentManager.commit {
            setReorderingAllowed(true)
            hideOtherFragments(except = target)
            show(fragment)
        }

        if (fragment is Refreshable) {
            fragment.onSelected()
        }

        currentTab = target
    }

    private fun prepareFragment(tab: MainTab): Fragment =
        tabs.getOrPut(tab) {
            tab.create().also { fragment ->
                supportFragmentManager.commit {
                    setReorderingAllowed(true)
                    add(R.id.fcv_main, fragment, tab.name)
                }
            }
        }

    private fun FragmentTransaction.hideOtherFragments(except: MainTab) {
        tabs
            .filterKeys { it != except }
            .forEach { (_, fragment) ->
                hide(fragment)
            }
    }

    interface Refreshable {
        fun onSelected()
    }
}
