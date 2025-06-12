/*
 * Copyright (c) <2025>, <zlingsmart technology Corporation>.
 * All rights reserved.
 * The use, copying, distribution, transmission and modification of this file
 * are allowed only with the written authorization of Zlingsmart Technology.
 */

package com.raite.crcc.systemui.ui.focus.indicator

import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.os.Messenger
import android.os.SystemProperties
import android.view.Display
import android.view.KeyEvent
import androidx.annotation.AnyThread
import androidx.annotation.UiThread
import com.raite.crcc.common.util.LogUtil
import com.raite.crcc.common.util.appContext
import com.raite.crcc.common.util.customScope
import com.raite.crcc.common.util.defaultLog
import com.raite.crcc.systemui.config.DisplayType.DISPLAY_TYPE_FAULT
import com.raite.crcc.systemui.config.DisplayType.DISPLAY_TYPE_HOME
import com.raite.crcc.systemui.config.DisplayType.DISPLAY_TYPE_HVAC
import com.raite.crcc.systemui.config.DisplayType.DISPLAY_TYPE_INFO
import com.raite.crcc.systemui.config.DisplayType.DISPLAY_TYPE_NAVIGATION_BAR
import com.raite.crcc.systemui.config.DisplayType.DISPLAY_TYPE_NONE
import com.raite.crcc.systemui.config.DisplayType.DISPLAY_TYPE_OTHER
import com.raite.crcc.systemui.config.DisplayType.DISPLAY_TYPE_SETTING
import com.raite.crcc.systemui.config.DisplayType.DISPLAY_TYPE_SYSTEM
import com.raite.crcc.systemui.config.DisplayType.SUB_DISPLAY_TYPE_A360
import com.raite.crcc.systemui.config.DisplayType.SUB_DISPLAY_TYPE_CAMERA
import com.raite.crcc.systemui.config.DisplayType.SUB_DISPLAY_TYPE_DISK
import com.raite.crcc.systemui.config.DisplayType.SUB_DISPLAY_TYPE_LOG
import com.raite.crcc.systemui.config.DisplayType.SUB_DISPLAY_TYPE_LOGIN
import com.raite.crcc.systemui.config.DisplayType.SUB_DISPLAY_TYPE_NAVIGATION_BAR
import com.raite.crcc.systemui.config.DisplayType.SUB_DISPLAY_TYPE_OTHER
import com.raite.crcc.systemui.config.DisplayType.SUB_DISPLAY_TYPE_STATUS_BAR
import com.raite.crcc.systemui.config.DisplayType.SUB_DISPLAY_TYPE_USE_MANAGER
import com.raite.crcc.systemui.config.DisplayType.SUB_DISPLAY_TYPE_VIDEO
import com.raite.crcc.systemui.config.ScreenConfig
import com.raite.crcc.systemui.provider.DefaultProviderManager
import com.raite.crcc.systemui.ui.focus.indicator.DisplayFocusIndicatorManager.Companion.ACTION_WINDOW_FOCUS_TRUE
import com.raite.crcc.systemui.ui.subscreen.ScreenController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

const val MSG_REQUEST_FOCUS = 1
const val MSG_CLEAR_FOCUS = 2

@UiThread
object GlobalDisplayFocusIndicatorManager {

    private const val TAG = "GlobalDisplayFocusIndicatorManager"

    private const val SYSTEM_PROP_FEATURE = "persist.debug.global_focus_indicator"
    private val mSystemPropFeatureLock = Any()
    private var mSystemPropFeature: Boolean = false

    private const val EXTRA_MESSENGER_BINDER = "messenger_binder"
    private const val EXTRA_VIEW_TYPE = "view_type"
    private const val EXTRA_ACTION = "action"
    private const val EXTRA_DISPLAY_ID = "display_id"
    private const val EXTRA_X = "x"
    private const val EXTRA_Y = "y"
    private const val EXTRA_WIDTH = "width"
    private const val EXTRA_HEIGHT = "height"
    private const val EXTRA_AT_TOP = "at_top"
    private const val EXTRA_AT_BOTTOM = "at_bottom"

    private const val EXTRA_KEY_CODE = "key_code"

    private val mUiHandler = Handler(Looper.getMainLooper())

    //最后焦点框显示在的屏幕id - 主屏 副屏
    private var mLastDisplayType = -1

    // 主屏中间应用是否显示底部的焦点
    private var mLastDisplayAtBottom: Boolean = false
    // 副屏中间应用是否显示底部的焦点
    private var mLastSubDisplayAtBottom: Boolean = false
    private var mLastSubDisplayAtTop: Boolean = false

    // 当前获取到 windowFocus的view
    private val mWindowFocusViewType = MutableStateFlow(DISPLAY_TYPE_NONE)
    val windowFocusView = mWindowFocusViewType.asStateFlow()

    // 请求获取焦点 中间应用的view
    private val mRequestFocusViewType = MutableStateFlow(DISPLAY_TYPE_NONE)
    val requestFocusViewType = mRequestFocusViewType.asStateFlow()

    private val mCustomScope = customScope(coroutineName = TAG)

    private val mDisplayMessengerMap: MutableMap<Int, IBinder?> = mutableMapOf(
        DISPLAY_TYPE_NAVIGATION_BAR to null,
        DISPLAY_TYPE_OTHER to null,
        DISPLAY_TYPE_HOME to null,
        DISPLAY_TYPE_SETTING to null,
        DISPLAY_TYPE_HVAC to null,
        DISPLAY_TYPE_FAULT to null,
        DISPLAY_TYPE_INFO to null,
        DISPLAY_TYPE_SYSTEM to null,
        SUB_DISPLAY_TYPE_STATUS_BAR to null,
        SUB_DISPLAY_TYPE_NAVIGATION_BAR to null,
        SUB_DISPLAY_TYPE_OTHER to null,
        SUB_DISPLAY_TYPE_CAMERA to null,
        SUB_DISPLAY_TYPE_A360 to null,
        SUB_DISPLAY_TYPE_VIDEO to null,
        SUB_DISPLAY_TYPE_DISK to null,
        SUB_DISPLAY_TYPE_LOG to null,
        SUB_DISPLAY_TYPE_LOGIN to null,
        SUB_DISPLAY_TYPE_USE_MANAGER to null,
    )

    /**
     * 每一个 display 对应一个 DisplayFocusIndicatorManager
     */
    private val mDisplayFocusIndicatorManagers = mutableMapOf<Int, DisplayFocusIndicatorManager>()

    /**
     * 获取目标 display 上的焦点管理器，如果没有找到则新建一个
     */
    @UiThread
    fun getOrCreateDisplayFocusIndicatorManager(displayId: Int): DisplayFocusIndicatorManager {
        var manager = mDisplayFocusIndicatorManagers[displayId]
        if (manager == null) {
            manager = DisplayFocusIndicatorManager(displayId)
            mDisplayFocusIndicatorManagers[displayId] = manager
        }
        return manager
    }

    fun getLastDisplayType(): Int = mLastDisplayType

    /**
     * 主副屏获取焦点后，清除对方的system ui焦点
     */
    fun cleanDisplayFocusView(displayId: Int): Boolean{
        if (mLastDisplayType != displayId) {
            when (displayId) {
                Display.DEFAULT_DISPLAY -> {
                    cleanDisplayFocusViewById(ScreenConfig.SECOND_DISPLAY_ID)
                }
                ScreenConfig.SECOND_DISPLAY_ID -> {
                    cleanDisplayFocusViewById(Display.DEFAULT_DISPLAY)
                }
            }
            mLastDisplayType = displayId
            return true
        }
        mLastDisplayType = displayId
        return false
    }

    private fun cleanDisplayFocusViewById(cleanDisplayId: Int) {
        LogUtil.i(TAG, "cleanDisplayFocusViewById cleanDisplayId:$cleanDisplayId")
        mUiHandler.post {
            getOrCreateDisplayFocusIndicatorManager(cleanDisplayId).showAtLocation(
                animate = false,
                location = IndicatorLocation(),
            )
        }
        if (cleanDisplayId == Display.DEFAULT_DISPLAY) {
            ScreenController.updateDisplayNaviFocus(false)
        } else if (cleanDisplayId == ScreenConfig.SECOND_DISPLAY_ID) {
            ScreenController.updateSubDisplayStatusFocus(false)
            ScreenController.updateSubDisplayNaviFocus(false)
        }
    }

    fun updateAppKeyCode(extras: Bundle?) {
        if (!mSystemPropFeature) {
            // 功能关闭
            return
        }

        extras?.let {
            val keyCode = extras.getInt(EXTRA_KEY_CODE, 0)
            val displayId = extras.getInt(EXTRA_DISPLAY_ID, 0)
            // @formatter:off
            LogUtil.i(TAG, "updateIndicatorInternal updateAppKeyCode keyCode:$keyCode " +
                    " displayId:$displayId atBottom:$mLastDisplayAtBottom " +
                    " subAtTop:$mLastSubDisplayAtTop subAtBottom:$mLastSubDisplayAtBottom" +
                    " lastDisplayType:$mLastDisplayType")
            // @formatter:on
            // 如果焦点不在自己屏幕上，需求清除焦点，并且自己屏上的app获取焦点
            if (cleanDisplayFocusView(displayId)) {
                val displayType = getDisplayType(displayId)
                if (displayType != DISPLAY_TYPE_NONE) {
                    sendMessageToDisplayType(displayType, MSG_REQUEST_FOCUS)
                }
                return
            }

            if (displayId == Display.DEFAULT_DISPLAY) {
                if (mLastDisplayAtBottom && keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                    mUiHandler.post {
                        ScreenController.updateDisplayNaviFocus(
                            focusable = true,
                            requestFocus = true,
                        )
                    }
                }
            } else if (displayId == ScreenConfig.SECOND_DISPLAY_ID) {
                if (mLastSubDisplayAtBottom && keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                    mUiHandler.post {
                        ScreenController.updateSubDisplayNaviFocus(
                            focusable = true,
                            requestFocus = true,
                        )
                    }
                }

                if (mLastSubDisplayAtTop && keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                    mUiHandler.post {
                        ScreenController.updateSubDisplayStatusFocus(
                            focusable = true,
                            requestFocus = true,
                        )
                    }
                }
            }

        }
    }

    @AnyThread
    fun updateIndicator(extras: Bundle?) {
        if (!mSystemPropFeature) {
            // 功能关闭
            return
        }

        // 触发 unparcel
        extras?.size()

        LogUtil.i(TAG, "updateIndicator extras:$extras")
        mUiHandler.post {
            updateIndicatorInternal(extras)
        }
    }

    @UiThread
    private fun updateIndicatorInternal(extras: Bundle?) {
        if (extras == null) {
            return
        }

        val messengerBinder = extras.getBinder(EXTRA_MESSENGER_BINDER)
        val viewType = extras.getInt(EXTRA_VIEW_TYPE, DISPLAY_TYPE_NONE)
        val focusAction = extras.getInt(EXTRA_ACTION, 0)
        val displayId = extras.getInt(EXTRA_DISPLAY_ID, 0)
        val x = extras.getFloat(EXTRA_X, 0f)
        val y = extras.getFloat(EXTRA_Y, 0f)
        val width = extras.getFloat(EXTRA_WIDTH, 0f)
        val height = extras.getFloat(EXTRA_HEIGHT, 0f)
        val atTop = extras.getBoolean(EXTRA_AT_TOP, false)
        val atBottom = extras.getBoolean(EXTRA_AT_BOTTOM, false)

        LogUtil.i(TAG, "updateIndicatorInternal viewType:$viewType action:$focusAction" +
                " displayId:$displayId x:$x y:$y width:$width height$height" +
                " atTop:$atTop atBottom:$atBottom binder:$messengerBinder" +
                " lastDisplayType:$mLastDisplayType")

        if (focusAction == ACTION_WINDOW_FOCUS_TRUE) {
            if (viewType != DISPLAY_TYPE_NONE) {
                mDisplayMessengerMap[viewType] = messengerBinder
            }
            mWindowFocusViewType.value = viewType
            mLastDisplayType = displayId
        }

        if (displayId == Display.DEFAULT_DISPLAY){
            mLastDisplayAtBottom = if (width != 0f && height != 0f) {
                atBottom
            } else {
                false
            }
        } else if (displayId == ScreenConfig.SECOND_DISPLAY_ID) {
            mLastSubDisplayAtBottom = if (width != 0f && height != 0f) {
                atBottom
            } else {
                false
            }

            mLastSubDisplayAtTop = if (width != 0f && height != 0f) {
                atTop
            } else {
                false
            }
        }

        getOrCreateDisplayFocusIndicatorManager(displayId).handleAction(
            messengerBinder = messengerBinder,
            action = focusAction,
            IndicatorLocation(x = x, y = y, width = width, height = height),
        )
    }

    fun sendMessenger(displayId: Int, msg: Int) {
        LogUtil.i(TAG, "sendMessenger displayId:$displayId msg:$msg")

        // 获取对应displayId的displayType
        val displayType = getDisplayType(displayId)

        if (displayType == DISPLAY_TYPE_NONE) {
            return
        }
        LogUtil.i(TAG, "sendMessenger displayType:$displayType")

        if (msg == MSG_REQUEST_FOCUS) {
            mRequestFocusViewType.value = displayType
        } else {
            mRequestFocusViewType.value = DISPLAY_TYPE_NONE
            sendMessageToDisplayType(displayType, msg)
        }
    }

    private fun getDisplayType(displayId: Int): Int{
        return when (displayId) {
            Display.DEFAULT_DISPLAY -> {
                var type = DefaultProviderManager.getDisplayType(appContext())
                if (type == DISPLAY_TYPE_HVAC) type = DISPLAY_TYPE_SYSTEM
                type
            }
            ScreenConfig.SECOND_DISPLAY_ID -> {
                val subType = DefaultProviderManager.getSubDisplayType(appContext())
                subType
            }
            else -> {
                LogUtil.w(TAG, "Unsupported displayId: $displayId")
                DISPLAY_TYPE_NONE
            }
        }
    }

    /**
     * 发送消息到指定的displayType对应的Messenger
     */
    private fun sendMessageToDisplayType(displayType: Int, msg: Int) {
        LogUtil.i(TAG, "sendMessageToDisplayType displayId:$displayType msg:$msg")
        mDisplayMessengerMap[displayType]?.let {
            LogUtil.i(TAG, "sendMessageToDisplayType it:$it")
            val targetMessenger = Messenger(it)
            val message = Message.obtain().apply { what = msg }
            kotlin.runCatching {
                targetMessenger.send(message)
            }.onFailure { it.defaultLog(TAG) }
        }
    }

    fun setRequestSystemFocusView(type: Int) {
        LogUtil.i(TAG, "setRequestSystemFocusView type:$type")
        mRequestFocusViewType.value = type
    }

    private fun registerSystemPropChangedCallback() {
        kotlin.runCatching {
            SystemProperties.addChangeCallback {
                LogUtil.i(TAG, "SystemProperties changed")
                loadSystemPropFeature()
            }
        }.onFailure { it.defaultLog(TAG) }
    }

    private fun loadSystemPropFeature() {
        synchronized(mSystemPropFeatureLock) {
            kotlin.runCatching {
                val systemPropFeature = SystemProperties.get(SYSTEM_PROP_FEATURE)
                mSystemPropFeature = systemPropFeature == "1"
                // @formatter:off
                LogUtil.i(TAG, "loadSystemPropFeature" +
                        " systemPropFeature:$systemPropFeature," +
                        " mSystemPropFeature:$mSystemPropFeature")
                // @formatter:on
            }.onFailure { it.defaultLog(TAG) }
        }
    }

    init {
        registerSystemPropChangedCallback()
        loadSystemPropFeature()
        mCustomScope.launch {
            launch {
                combine(
                    mWindowFocusViewType,
                    mRequestFocusViewType,
                ) { windowFocus, requestFocus ->
                    Pair(windowFocus, requestFocus)
                }.collectLatest {
                    LogUtil.i(TAG, "windowFocusView request:$it")
                    // 获得window focus，请求获取焦点的view才能去请求焦点
                    if (it.first != DISPLAY_TYPE_NONE && it.first == it.second) {
                        sendMessageToDisplayType(it.second, MSG_REQUEST_FOCUS)
                        mRequestFocusViewType.value = DISPLAY_TYPE_NONE
                    }
                }
            }
        }
    }

}