/*
 * Copyright (c) <2025>, <zlingsmart technology Corporation>.
 * All rights reserved.
 * The use, copying, distribution, transmission and modification of this file
 * are allowed only with the written authorization of Zlingsmart Technology.
 */

package com.raite.crcc.systemui.utils

import android.os.SystemProperties

/**
 * @Author zl
 * @Date 2023/10/17
 * @Description
 */

typealias Block = () -> Unit

typealias BlockInt = (index: Int) -> Unit

typealias BlockBoolean = (index: Boolean) -> Unit
internal fun isSQ() = SystemProperties.get("ro.product.project.name") == "SXQC-Cockpit"