/*
 * Copyright (c) <2025>, <zlingsmart technology Corporation>.
 * All rights reserved.
 * The use, copying, distribution, transmission and modification of this file
 * are allowed only with the written authorization of Zlingsmart Technology.
 */

package com.raite.crcc.systemui.utils

import android.os.Handler
import android.os.Looper
import com.raite.crcc.systemui.utils.Block

/**
 * @Author zl
 * @Date 2023/12/20
 * @Description
 */

internal fun delayExecute(block: Block) {
    Handler(Looper.getMainLooper()).post(block)
}

internal fun delayExecute(block: Block, delayMillis: Long) {
    Handler(Looper.getMainLooper()).postDelayed({ block() }, delayMillis)
}