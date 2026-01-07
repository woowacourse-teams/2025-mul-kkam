package com.mulkkam.data.local.userdefaults

import platform.Foundation.NSUserDefaults

class MembersUserDefaults {
    private val userDefaults: NSUserDefaults = NSUserDefaults.standardUserDefaults

    val isFirstLaunch: Boolean
        get() = !userDefaults.boolForKey(KEY_FIRST_LAUNCH_COMPLETED)

    fun saveIsFirstLaunch() {
        userDefaults.setBool(true, KEY_FIRST_LAUNCH_COMPLETED)
    }

    companion object {
        private const val KEY_FIRST_LAUNCH_COMPLETED: String = "FIRST_LAUNCH_COMPLETED"
    }
}
