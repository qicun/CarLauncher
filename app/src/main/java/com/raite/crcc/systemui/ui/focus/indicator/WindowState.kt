/*
 * Copyright (c) <2025>, <zlingsmart technology Corporation>.
 * All rights reserved.
 * The use, copying, distribution, transmission and modification of this file
 * are allowed only with the written authorization of Zlingsmart Technology.
 */

package com.raite.crcc.systemui.ui.focus.indicator

import android.os.IBinder
import com.raite.crcc.common.util.defaultLog

class WindowState(
    /**
     * 1、window 的唯一标识
     * 2、向 window 客户端发送消息
     */
    val messengerBinder: IBinder,
    /**
     * 标识客户端进程是否工作正常
     */
    var clientBinderDied: Boolean = false,
    /**
     * 该 window 在该 display 上是否有 focus
     */
    var focusWindow: Boolean,
    /**
     * 该 window 上的 focus view
     */
    var focusView: IndicatorLocation,
) : IBinder.DeathRecipient {

    private val mObjectTag by lazy {
        "${javaClass.simpleName}@${System.identityHashCode(this)}"
    }

    init {
        kotlin.runCatching {
            messengerBinder.linkToDeath(this, 0)
        }.onFailure {
            it.defaultLog(mObjectTag)
            clientBinderDied = true
        }
    }

    override fun binderDied() {
        // 客户端进程挂掉了
        clientBinderDied = true
    }

}