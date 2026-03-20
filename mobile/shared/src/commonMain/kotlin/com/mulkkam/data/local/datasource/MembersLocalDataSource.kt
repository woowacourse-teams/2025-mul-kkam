package com.mulkkam.data.local.datasource

interface MembersLocalDataSource {
    val isFirstLaunch: Boolean

    fun saveIsFirstLaunch()
}
