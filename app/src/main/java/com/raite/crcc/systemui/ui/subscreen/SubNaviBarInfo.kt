/*
 * Copyright (c) <2025>, <zlingsmart technology Corporation>.
 * All rights reserved.
 * The use, copying, distribution, transmission and modification of this file
 * are allowed only with the written authorization of Zlingsmart Technology.
 */

package com.raite.crcc.systemui.ui.subscreen

/**
 * @Author zl
 * @Date 2023/9/14
 * @Description
 */
data class SubNaviBarInfo (
    var updateType: Int,
    var resId: Int? = null,
    var value: Float? = null,
    var boolValue: Boolean = false
)