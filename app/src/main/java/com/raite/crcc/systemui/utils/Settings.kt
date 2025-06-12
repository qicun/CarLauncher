/*
 * Copyright (c) <2025>, <zlingsmart technology Corporation>.
 * All rights reserved.
 * The use, copying, distribution, transmission and modification of this file
 * are allowed only with the written authorization of Zlingsmart Technology.
 */

package com.raite.crcc.systemui.utils

import android.content.Context
import android.net.Uri
import android.provider.Settings
import com.raite.crcc.systemui.config.UserConfig

/**
 * @Author zl
 * @Date 2023/9/26
 * @Description
 */

fun Context.getSystemInt(key: String, value: Int): Int {
    return Settings.System.getInt(this.contentResolver, key, value)
}

fun Context.setSystemInt(key: String, value: Int) {
    Settings.System.putInt(this.contentResolver, key, value)
}

fun Context.getGlobalInt(key: String, value: Int): Int {
    return Settings.Global.getInt(this.contentResolver, key, value)
}

fun Context.getGlobalString(key: String): String {
    return Settings.Global.getString(this.contentResolver, key) ?: ""
}

fun Context.setGlobalInt(key: String, value: Int) {
    Settings.Global.putInt(this.contentResolver, key, value)
}

fun getUserLoginUri(): Uri {
    return Settings.Global.getUriFor(UserConfig.SETTINGS_USER_LOGIN)
}

fun getUserNameUri(): Uri {
    return Settings.Global.getUriFor(UserConfig.SETTINGS_USER_NAME)
}

fun getUserLevelUri(): Uri {
    return Settings.Global.getUriFor(UserConfig.SETTINGS_USER_LEVEL)
}

fun getTimeFormatUri(): Uri {
    return Settings.System.getUriFor(Settings.System.TIME_12_24)
}

fun Context.getTimeFormatString(): String {
    return Settings.System.getString(this.contentResolver, Settings.System.TIME_12_24) ?: "12"
}