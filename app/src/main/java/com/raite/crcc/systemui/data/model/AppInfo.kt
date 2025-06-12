package com.raite.crcc.systemui.data.model

import android.graphics.drawable.Drawable

data class AppInfo(
    val label: String,
    val packageName: String,
    val icon: Drawable,
    var showBadge: Boolean = false
) 