package com.raite.crcc.systemui

import android.app.Application
import com.raite.crcc.systemui.util.ContextUtil

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        ContextUtil.context = applicationContext
    }
}