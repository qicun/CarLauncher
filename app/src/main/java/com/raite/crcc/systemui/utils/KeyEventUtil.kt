/*
 * Copyright (c) <2025>, <zlingsmart technology Corporation>.
 * All rights reserved.
 * The use, copying, distribution, transmission and modification of this file
 * are allowed only with the written authorization of Zlingsmart Technology.
 */

package com.raite.crcc.systemui.utils

import android.hardware.input.InputManager
import android.os.SystemClock
import android.view.InputDevice
import android.view.KeyCharacterMap
import android.view.KeyEvent

/**
 * @Author zl
 * @Date 2023/9/13
 * @Description
 * 按键相关
 */
object KeyEventUtil {

    fun backHome(displayId: Int) {
        val event = KeyEvent(
            System.currentTimeMillis(),
            SystemClock.uptimeMillis(),
            KeyEvent.ACTION_UP,
            KeyEvent.KEYCODE_HOME,
            0,
            0,
            KeyCharacterMap.VIRTUAL_KEYBOARD,
            0,
            KeyEvent.FLAG_FROM_SYSTEM or KeyEvent.FLAG_VIRTUAL_HARD_KEY,
            InputDevice.SOURCE_KEYBOARD
        )
        event.displayId = displayId
        InputManager.getInstance()
            .injectInputEvent(event, InputManager.INJECT_INPUT_EVENT_MODE_ASYNC)
    }
}