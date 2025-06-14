package com.raite.crcc.systemui.data.model

import android.graphics.drawable.Drawable

data class AppInfo(
    val label: String,
    val packageName: String,
    val icon: Drawable,
    var showBadge: Boolean = false,
    val itemType: Int = TYPE_APP
) {
    companion object {
        const val TYPE_APP = 0
        const val TYPE_WALLPAPER_ACTION = 1
    }
} 