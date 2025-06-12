/*
 * Copyright (c) <2025>, <zlingsmart technology Corporation>.
 * All rights reserved.
 * The use, copying, distribution, transmission and modification of this file
 * are allowed only with the written authorization of Zlingsmart Technology.
 */

package com.raite.crcc.systemui.ui.navi

/**
 * @Author zl
 * @Date 2023/9/1
 * @Description
 */
data class NaviBarInfo(
    var updateType: Int,
    var resId: Int? = null,
    var value: Float? = null,
    var boolValue: Boolean = false,
    var value1: Int = 0,
)
