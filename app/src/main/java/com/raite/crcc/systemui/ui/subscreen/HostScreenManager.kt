/*
 * Copyright (c) <2025>, <zlingsmart technology Corporation>.
 * All rights reserved.
 * The use, copying, distribution, transmission and modification of this file
 * are allowed only with the written authorization of Zlingsmart Technology.
 */

package com.raite.crcc.systemui.ui.subscreen

import android.annotation.DisplayContext
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.graphics.PixelFormat
import android.os.Handler
import android.os.Looper
import android.view.Display
import android.view.Gravity
import android.view.WindowManager
import com.raite.crcc.systemui.CmdQueue
import com.raite.crcc.systemui.R
import com.raite.crcc.systemui.config.DisplayType.DISPLAY_TYPE_NAVIGATION_BAR
import com.raite.crcc.systemui.config.ScreenConfig
import com.raite.crcc.systemui.ui.focus.indicator.GlobalDisplayFocusIndicatorManager
import com.raite.crcc.systemui.ui.focus.indicator.MSG_CLEAR_FOCUS
import com.raite.crcc.systemui.ui.focus.indicator.MSG_REQUEST_FOCUS
import com.raite.crcc.systemui.ui.navi.NavigationBarView
import com.raite.crcc.systemui.ui.status.StatusBarView
import com.raite.crcc.systemui.utils.DisplayWindowManager
import com.raite.crcc.systemui.utils.Plog
import com.raite.crcc.systemui.utils.getString

/**
 * @Author zl
 * @Date 2023/8/21
 * @Description 主屏控制类
 */
class HostScreenManager(
    @DisplayContext private val mDisplayContext: Context,
    @DisplayWindowManager private val mDisplayWindowManager: WindowManager,
    private val mCommandQueue: CmdQueue, displayId: Int = Display.DEFAULT_DISPLAY
) {

    private val mStatusBarParams = WindowManager.LayoutParams()
    private val mNavigationBarParams = WindowManager.LayoutParams()
    private var mDisplayId = displayId
    private var mStatusbar: StatusBarView? = null
    private var mNavibar: NavigationBarView? = null
    private val TAG = "HostScreenManager"
    private var mWindowAdded = false
    private val mUiHandler = Handler(Looper.getMainLooper())

    fun visible() = mWindowAdded

    fun setConfigurationChanged(configuration: Configuration) {
        mStatusbar?.onConfigurationChanged(configuration)
        mNavibar?.onConfigurationChanged(configuration)
        Plog.i(TAG, "${TAG}---换肤改变回调====${configuration}")
    }

    fun initHostStatusBar() {
        Plog.i(TAG, "init-Host-StatusBar")
        mStatusBarParams.title = getString(R.string.statusbar_title)
        mStatusBarParams.format = PixelFormat.TRANSPARENT
        mStatusBarParams.type = WindowManager.LayoutParams.TYPE_STATUS_BAR
        mStatusBarParams.flags =
            mStatusBarParams.flags or (WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    or WindowManager.LayoutParams.FLAG_SPLIT_TOUCH
                    or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                    or WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        mStatusBarParams.flags =
            mStatusBarParams.flags and WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL.inv()
        mStatusBarParams.width = WindowManager.LayoutParams.MATCH_PARENT
        mStatusBarParams.height = ScreenConfig.STATUS_BAR_HEIGHT
        mStatusBarParams.gravity = Gravity.TOP
        mStatusBarParams.windowAnimations = 0
        mStatusbar =
            StatusBarView(mDisplayContext, mCommandQueue, mDisplayId, mDisplayWindowManager)

        runCatching {
            mDisplayWindowManager.addView(mStatusbar?.getRootView(), mStatusBarParams)
            mWindowAdded = true
        }
    }

    @SuppressLint("WrongConstant")
    fun initHostNavigationBar() {
        Plog.i(TAG, "init-Host-NavigationBar")
        mNavigationBarParams.title = getString(R.string.navigationbar_title)
        mNavigationBarParams.flags = mNavigationBarParams.flags or
                (WindowManager.LayoutParams.FLAG_SPLIT_TOUCH
                        or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        or WindowManager.LayoutParams.FLAG_BLUR_BEHIND
                        or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH)
        mNavigationBarParams.format = PixelFormat.TRANSPARENT
        // WindowManager.LayoutParams.TYPE_NAVIGATION_BAR = 2019;
        mNavigationBarParams.type = 2019
        mNavigationBarParams.width = WindowManager.LayoutParams.MATCH_PARENT
        mNavigationBarParams.height = ScreenConfig.NAVIGATION_BAR_HEIGHT
        mNavigationBarParams.gravity = Gravity.BOTTOM
        mNavigationBarParams.windowAnimations = 0
        mNavibar = NavigationBarView(mDisplayContext, mCommandQueue, mDisplayId)

        runCatching {
            mDisplayWindowManager.addView(mNavibar?.getRootView(), mNavigationBarParams)
            mWindowAdded = true
        }
    }

    fun updateHostNavigationBar(focusable: Boolean, requestFocus:Boolean) {
        val params = mNavibar?.getRootView()?.layoutParams as WindowManager.LayoutParams
        val flagToNotFocusable = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        val flagSt = params.flags and flagToNotFocusable

        Plog.i(TAG, "updateHostNavigationBar update-Host-NavigationBar focusable:$focusable " +
                " requestFocus:$requestFocus flagSt:$flagSt")

        if (focusable && flagSt == 0 && requestFocus.not()) {
                return
        }

        if(focusable.not() && flagSt != 0 && requestFocus.not()){
            return
        }

        runCatching {
            if (focusable) {
                Plog.i(TAG, "updateHostNavigationBar requestFocus")
                params.flags = params.flags and WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE.inv()
                mDisplayWindowManager.updateViewLayout(mNavibar?.getRootView(), params)
                mNavibar?.getHomeView()?.isFocusable = true
                if (requestFocus) {
                    // 清除焦点
                    GlobalDisplayFocusIndicatorManager.sendMessenger(
                        Display.DEFAULT_DISPLAY,
                        MSG_CLEAR_FOCUS,
                    )
                    // 获取焦点
                    GlobalDisplayFocusIndicatorManager.setRequestSystemFocusView(
                        DISPLAY_TYPE_NAVIGATION_BAR,
                    )
                }

            } else {
                Plog.i(TAG, "updateHostNavigationBar clearFocus")
                params.flags = params.flags or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                mDisplayWindowManager.updateViewLayout(mNavibar?.getRootView(), params)
                mNavibar?.getHomeView()?.isFocusable = false
                if (requestFocus) {
                    GlobalDisplayFocusIndicatorManager.sendMessenger(
                        Display.DEFAULT_DISPLAY,
                        MSG_REQUEST_FOCUS,
                    )
                }
            }
        }
    }
}