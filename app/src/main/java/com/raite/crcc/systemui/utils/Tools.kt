/*
 * Copyright (c) <2025>, <zlingsmart technology Corporation>.
 * All rights reserved.
 * The use, copying, distribution, transmission and modification of this file
 * are allowed only with the written authorization of Zlingsmart Technology.
 */

package com.raite.crcc.systemui.utils

import android.app.ActivityOptions
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Display
import com.raite.crcc.systemui.R
import com.raite.crcc.systemui.config.ServiceConfig
import java.text.DecimalFormat

/**
 * @Author lsh
 * @Date 2023/7/12 11:23
 * @Description
 */
object Tools {

    const val TAG = "Tools"

    fun logScreenInfo(context: Context) {
        val screen = "屏幕宽高px：${getScreenW(context)},${getScreenH(context)}"
        val screenDp =
            ("屏幕宽高dp：${(getScreenW(context) / getScreenDensity(context)).toInt()}" +
                    ",${(getScreenH(context) / getScreenDensity(context)).toInt()}")
        Plog.i(TAG, screen)
        Plog.i(TAG, "屏幕密度：${getScreenDensity(context)}")
        Plog.i(TAG, screenDp)
        Plog.i(TAG, "dp10=${context.resources.getDimension(R.dimen.dp_10)}")
        Plog.i(TAG, "Android OS=${Build.VERSION.RELEASE},SDK_INT=${Build.VERSION.SDK_INT}")
    }

    fun printDimens() {
        val builder = StringBuilder()
        for (i in 10..50) {
            val x = getDoubleFormat(i / 1.5)
            builder.append("<dimen name=\"dp${i}\">${x}px</dimen>\n")
        }
        Plog.i(TAG, builder.toString())
    }

    private fun getDoubleFormat(res: Double): String? {
        val df = DecimalFormat("#")
        return df.format(res)
    }

    fun openApp(
        context: Context,
        packageName: String,
        className: String? = null,
        displayId: Int = Display.DEFAULT_DISPLAY,
        key: String = "", value: Int = 0
    ): Boolean {
        try {
            if (className == null) {
                val packageManager: PackageManager = context.packageManager
                val intent = packageManager.getLaunchIntentForPackage(packageName)
                if (intent != null) {
                    context.startActivity(intent)
                } else {
                    loge("openApp err: has not found %s launcher activity${packageName}")
                    return false
                }
            } else {
                val intent = Intent()
                if (key.isNotEmpty()) {
                    val bundle = Bundle()
                    bundle.putInt(key, value)
                    intent.putExtras(bundle)
                }
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                intent.component = ComponentName(packageName, className)
                val options = ActivityOptions.makeBasic()
                options.setLaunchDisplayId(displayId)
                context.startActivity(intent, options.toBundle())
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
        return true
    }

    fun openApp(
        context: Context,
        intent: Intent,
        displayId: Int = context.display.displayId,
    ) {
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        val options = ActivityOptions.makeBasic()
        options.launchDisplayId = displayId
        context.startActivity(intent, options.toBundle())
    }


    fun openService(
        context: Context,
        packageName: String,
        className: String,
        key: String = "",
        value: String = ""
    ) {
        val intent = Intent()
        if (key.isNotEmpty()) {
            val bundle = Bundle()
            bundle.putString(key, value)
            intent.putExtras(bundle)
        }
        intent.component = ComponentName(packageName, className)
        context.startService(intent)
    }

    private fun getScreenW(context: Context): Int {
        val wm = ServiceConfig.getWindowManagerByUiContext(context)
        return wm.currentWindowMetrics.bounds.right
    }

    private fun getScreenH(context: Context): Int {
        val wm = ServiceConfig.getWindowManagerByUiContext(context)
        return wm.currentWindowMetrics.bounds.bottom
    }

    private fun getScreenDensity(context: Context): Float {
        return context.applicationContext.resources.displayMetrics.density
    }
}