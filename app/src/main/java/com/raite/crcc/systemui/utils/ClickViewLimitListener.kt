/*
 * Copyright (c) <2025>, <zlingsmart technology Corporation>.
 * All rights reserved.
 * The use, copying, distribution, transmission and modification of this file
 * are allowed only with the written authorization of Zlingsmart Technology.
 */

package com.raite.crcc.systemui.utils

import android.view.View
import android.view.View.OnClickListener

/**
 * @Author lsh
 * @Date 2023/8/2 11:52
 * @Description normal click interval is 800ms
 */
abstract class ClickViewLimitListener(private var timeInterval:Long = 800L) : OnClickListener {
    private var lastClickTime: Long = 0

    override fun onClick(v: View?) {
        val nowTime = System.currentTimeMillis()
        if (nowTime - lastClickTime > timeInterval) {
            lastClickTime = nowTime
            onClickView(v)
        }
    }

    abstract fun onClickView(v: View?)
}