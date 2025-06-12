package com.raite.crcc.systemui

import com.raite.crcc.common.util.LogUtil

object BuildInfo {

    private const val TAG = "BuildInfo"

    private fun printInfo() {
        LogUtil.i(
            TAG,
            arrayOf(
                BuildConfig.APPLICATION_ID,
                BuildConfig.BUILD_TYPE,
                BuildConfig.DEBUG,
                BuildConfig.VERSION_CODE,
                BuildConfig.VERSION_NAME,
            ).joinToString("|"),
        )
    }

    init {
        printInfo()
    }

}