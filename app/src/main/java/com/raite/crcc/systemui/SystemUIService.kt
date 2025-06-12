/*
 * Copyright (c) <2025>, <zlingsmart technology Corporation>.
 * All rights reserved.
 * The use, copying, distribution, transmission and modification of this file
 * are allowed only with the written authorization of Zlingsmart Technology.
 */

package com.raite.crcc.systemui

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.raite.crcc.systemui.utils.loge

class SystemUIService : Service() {

    // adb shell am startservice -n com.raite.crcc.systemui/com.raite.crcc.systemui.SystemUIService

    override fun onBind(intent: Intent?): IBinder? = null
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int = START_STICKY

    override fun onCreate() {
        super.onCreate()
        if (SystemUiMain.getInstance().isSystemUiVisible()) {
            loge("SB & NB 已经启动~~~~~")
        } else {
            SystemUiMain.getInstance().start()
        }
    }
}