package com.mulkkam.data.local.datasource

import com.mulkkam.data.local.userdefaults.MembersUserDefaults

class MembersLocalDataSourceImpl(
    private val membersUserDefaults: MembersUserDefaults,
) : MembersLocalDataSource {
    override val isFirstLaunch: Boolean
        get() = membersUserDefaults.isFirstLaunch

    override fun saveIsFirstLaunch() {
        membersUserDefaults.saveIsFirstLaunch()
    }
}
