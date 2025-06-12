/*
 * Copyright (c) <2025>, <zlingsmart technology Corporation>.
 * All rights reserved.
 * The use, copying, distribution, transmission and modification of this file
 * are allowed only with the written authorization of Zlingsmart Technology.
 */

package com.raite.crcc.systemui.ui.subscreen

import android.annotation.DisplayContext
import android.annotation.SuppressLint
import android.annotation.UiContext
import android.content.Context
import android.content.res.Configuration
import android.view.Display
import android.view.WindowManager
import com.raite.crcc.systemui.CmdQueue
import com.raite.crcc.systemui.R
import com.raite.crcc.systemui.config.ScreenConfig
import com.raite.crcc.systemui.config.ServiceConfig
import com.raite.crcc.systemui.utils.DisplayWindowManager
import com.raite.crcc.systemui.utils.Plog

/**
 * @Author zl
 * @Date 2023/8/21
 * @Description 主副屏控制类
 */
@SuppressLint("StaticFieldLeak")
object ScreenController {

    const val TAG = "ScreenController"
    private var mHostScreenManager: HostScreenManager? = null
    private var mSubScreenManager: SubScreenManager? = null
    private val mDisplayList = arrayListOf<Display>()

    fun isVisible(): Boolean {
        return mHostScreenManager?.visible() == true &&
                mSubScreenManager?.visible() == true
    }

    fun initConfigurationChanged(configuration: Configuration) {
        Plog.e(TAG, "$TAG--3--换肤改变回调====${configuration}")
        mHostScreenManager?.setConfigurationChanged(configuration)
        mSubScreenManager?.setConfigurationChanged(configuration)
    }

    fun initScreen(@UiContext uiContext: Context, mCommandQueue: CmdQueue?) {
        synchronized(mDisplayList) {
            val mDisplayManager = ServiceConfig.getDisplayManager(uiContext)
            val displays = mDisplayManager.displays
            Plog.w(TAG, "一共多少个屏幕设备 : ${displays.size}")
            for (display in displays) {
                mDisplayList.add(display)
                addSystemUIOnDisplayLocked(uiContext, mCommandQueue, display)
            }
        }
    }

    fun onDisplayReady(@UiContext uiContext: Context, mCommandQueue: CmdQueue?, displayId: Int) {
        synchronized(mDisplayList) {
            Plog.w(TAG, "onDisplayReady $displayId")
            mDisplayList.forEach {
                if (displayId == it.displayId) {
                    return
                }
            }
            val mDisplayManager = ServiceConfig.getDisplayManager(uiContext)
            val display = mDisplayManager.getDisplay(displayId)
            mDisplayList.add(display)
            addSystemUIOnDisplayLocked(uiContext, mCommandQueue, display)
        }
    }

    /**
     * 将SystemUI添加到对应display上
     * 只添加到主屏(displayId=0)和副屏(displayId=2)上
     */
    private fun addSystemUIOnDisplayLocked(
        @UiContext uiContext: Context,
        mCommandQueue: CmdQueue?,
        display: Display
    ) {
        Plog.w(TAG, "addSystemUIOnDisplay ${display.displayId}")
        if (display.displayId == Display.DEFAULT_DISPLAY
            || display.displayId == ScreenConfig.SECOND_DISPLAY_ID
        ) {
            val mDisplayContext: Context = uiContext.createDisplayContext(display)
            val mDisplayWindowManager =
                ServiceConfig.getWindowManagerByDisplayContext(mDisplayContext)
            mDisplayContext.setTheme(R.style.Theme_App)
            initUI(mDisplayContext, mDisplayWindowManager, mCommandQueue!!, display.displayId)
        }
    }

    /**初始化主/副屏**/
    private fun initUI(
        @DisplayContext mDisplayContext: Context,
        @DisplayWindowManager mDisplayWindowManager: WindowManager,
        mCommandQueue: CmdQueue, displayId: Int
    ) {
        if (displayId == Display.DEFAULT_DISPLAY) {
            mHostScreenManager = HostScreenManager(
                mDisplayContext, mDisplayWindowManager,
                mCommandQueue, displayId
            )
            mHostScreenManager?.initHostStatusBar()
            mHostScreenManager?.initHostNavigationBar()
        } else {
            mSubScreenManager = SubScreenManager(
                mDisplayContext, mDisplayWindowManager,
                mCommandQueue, displayId
            )
            mSubScreenManager?.initSubStatusBar()
            mSubScreenManager?.initSubNavigationBar()
        }
    }

    fun updateDisplayNaviFocus(focusable: Boolean, requestFocus:Boolean = false) {
        Plog.w(TAG, "updateDisplayNaviFocus focusable:$focusable requestFocus:$requestFocus")
        mHostScreenManager?.updateHostNavigationBar(focusable, requestFocus)
    }

    fun updateSubDisplayStatusFocus(focusable: Boolean, requestFocus:Boolean = false) {
        Plog.w(TAG, "updateSubDisplayStatusFocus focusable:$focusable requestFocus:$requestFocus")
        mSubScreenManager?.updateSubStatusBar(focusable, requestFocus)
    }

    fun updateSubDisplayNaviFocus(focusable: Boolean, requestFocus:Boolean = false) {
        Plog.w(TAG, "updateSubDisplayNaviFocus focusable:$focusable requestFocus:$requestFocus")
        mSubScreenManager?.updateSubNavigationBar(focusable, requestFocus)
    }
}