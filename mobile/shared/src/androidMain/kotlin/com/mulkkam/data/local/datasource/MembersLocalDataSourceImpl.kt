package com.mulkkam.data.local.datasource

import com.mulkkam.data.local.preference.MembersPreference

class MembersLocalDataSourceImpl(
    private val membersPreference: MembersPreference,
) : MembersLocalDataSource {
    override val isFirstLaunch: Boolean
        get() = membersPreference.isFirstLaunch

    override fun saveIsFirstLaunch() {
        membersPreference.saveIsFirstLaunch()
    }
}
