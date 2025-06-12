/*
 * Copyright (c) <2025>, <zlingsmart technology Corporation>.
 * All rights reserved.
 * The use, copying, distribution, transmission and modification of this file
 * are allowed only with the written authorization of Zlingsmart Technology.
 */

package com.raite.crcc.systemui.ui.focus.indicator

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager
import androidx.annotation.UiThread
import com.raite.crcc.common.util.appContext

/**
 * display 全局焦点指示器
 */
@SuppressLint("WrongConstant")
@UiThread
class DisplayFocusIndicatorManager(val displayId: Int) {

    companion object {

        /**
         * focus indicator 窗口层级, 比所有可以获取焦点的窗口的层级更高
         */
        private const val WINDOW_TYPE = 2015

        private const val NO_PADDING = 0f

        private const val INDICATOR_PADDING = 13f

        // 一个非空的 view 获取到了 focus. view tree global focus 报告的 newFocus 是一个非空 view
        const val ACTION_NEW_FOCUS_VIEW = 1

        // view tree global focus change 报告的 newFocus 是 null view
        const val ACTION_NEW_FOCUS_VIEW_NULL = 2

        // window focus 报告 hasFocus 为 true
        const val ACTION_WINDOW_FOCUS_TRUE = 3

        // window focus 报告 hasFocus 为 false
        const val ACTION_WINDOW_FOCUS_FALSE = 4
    }

    private val mFocusIndicatorLayout: FocusIndicatorLayout

    /**
     * 记录该 display 上的全部 window
     * mWindowStateMap 中至多只有一个 focus window
     */
    private val mWindowStateMap = mutableMapOf<IBinder, WindowState>()

    /**
     * 找到该 display 上的 focus window
     */
    @UiThread
    private fun findFocusWindow(): WindowState? {
        // 从 mWindowStateMap 获取 focus window
        mWindowStateMap.forEach{ (_, value) ->
            if (value.focusWindow)
                return value
        }
        return null
    }

    @UiThread
    fun handleAction(messengerBinder: IBinder?, action: Int, location: IndicatorLocation) {
        if (messengerBinder == null) {
            return
        }
        // 更新 mWindowStateMap
        setBinderWindowState(messengerBinder, action, location)
        // 更新 FocusWindow
        if (action == ACTION_WINDOW_FOCUS_TRUE) {
            updateFocusWindow(messengerBinder)
        }

        val focusWindow = findFocusWindow()
        if (focusWindow == null) {
            // 没有 focus window, 不显示 focus indicator
            showAtLocation(animate = false, location = IndicatorLocation())
        } else {
            // 显示 focus indicator
            showAtLocation(animate = true, location = focusWindow.focusView)
        }
    }

    private fun updateFocusWindow(messengerBinder: IBinder) {
        mWindowStateMap.forEach{ (key, value) ->
            value.focusWindow = key == messengerBinder
        }
    }

    private fun setBinderWindowState(
        messengerBinder: IBinder, action: Int, location: IndicatorLocation,
    ) {
        val state = getOrCreateBinderWindowState(messengerBinder, action, location)
        state.focusView = location
        when (action) {
            ACTION_WINDOW_FOCUS_TRUE -> {
                state.focusWindow = true
            }
            ACTION_WINDOW_FOCUS_FALSE -> {
                state.focusWindow = false
            }
        }
        mWindowStateMap[messengerBinder] = state
    }

    private fun getOrCreateBinderWindowState(
        messengerBinder: IBinder, action: Int, location: IndicatorLocation,
    ): WindowState {
        val windowState = mWindowStateMap.getOrPut(messengerBinder) { WindowState (
            messengerBinder = messengerBinder,
            focusWindow = action == ACTION_WINDOW_FOCUS_TRUE,
            focusView = location,
        ) }
        return windowState
    }

    /**
     * 将焦点指示器显示到指定位置
     *
     * @param animate 是否使用动画
     * @param location 目标位置
     */
    @UiThread
    fun showAtLocation(animate: Boolean, location: IndicatorLocation) {
        val padding = if (location.width == 0f || location.height == 0f) {
            NO_PADDING
        } else {
            INDICATOR_PADDING
        }
        mFocusIndicatorLayout.showAtLocation(
            animate = animate,
            location = location.padding(padding),
        )
    }

    init {
        var context = appContext()
        val displayManager = context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
        val display = displayManager.getDisplay(displayId)
        context = context.createDisplayContext(display)
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val attrs = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
        )
        attrs.gravity = Gravity.START or Gravity.TOP
        attrs.type = WINDOW_TYPE
        attrs.format = PixelFormat.TRANSPARENT
        // @formatter:off
        attrs.flags = (WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)
        // @formatter:on

        mFocusIndicatorLayout = FocusIndicatorLayout(context)
        windowManager.addView(mFocusIndicatorLayout, attrs)
    }

}