/*
 * Copyright (c) <2025>, <zlingsmart technology Corporation>.
 * All rights reserved.
 * The use, copying, distribution, transmission and modification of this file
 * are allowed only with the written authorization of Zlingsmart Technology.
 */

package com.raite.crcc.systemui.config

import com.raite.crcc.systemui.R
import com.raite.crcc.systemui.util.ContextUtil

/**
 * @Author zl
 * @Date 2023/8/22
 * @Description
 */
object ScreenConfig {

    val STATUS_BAR_HEIGHT = ContextUtil.context.resources.getDimensionPixelSize(
        com.android.internal.R.dimen.status_bar_height
    )

    val NAVIGATION_BAR_HEIGHT = ContextUtil.context.resources.getDimensionPixelSize(
        com.android.internal.R.dimen.navigation_bar_height
    )

    val PANEL_HEIGHT =
        ContextUtil.context.resources.getDimension(R.dimen.panel_height).toInt()

    /** 3588默认副屏displayId */
    const val SECOND_DISPLAY_ID = 2

}