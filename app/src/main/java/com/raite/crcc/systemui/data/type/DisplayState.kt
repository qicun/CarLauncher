/*
 * Copyright (c) <2025>, <zlingsmart technology Corporation>.
 * All rights reserved.
 * The use, copying, distribution, transmission and modification of this file
 * are allowed only with the written authorization of Zlingsmart Technology.
 */

package com.raite.crcc.systemui.data.type

import com.raite.crcc.systemui.R
import com.raite.crcc.systemui.config.DisplayType
import com.raite.crcc.systemui.config.SettingsConfig

/**
 * 主屏显示
 */
sealed class DisplayState(val viewId: Int = 0, val typeId :Int, val classAction: String = "") {
    data object Unknown : DisplayState(typeId = DisplayType.DISPLAY_TYPE_NONE)
    data object Home : DisplayState(R.id.homeBtn, DisplayType.DISPLAY_TYPE_HOME)
    data object Setting : DisplayState(
        R.id.setBtn, DisplayType.DISPLAY_TYPE_SETTING, SettingsConfig.ACTION_SETTING_FRAGMENT_OPEN
    )
    data object Hvac : DisplayState(
        R.id.airBtn, DisplayType.DISPLAY_TYPE_HVAC, SettingsConfig.ACTION_SYSTEM_FRAGMENT_HVAC_OPEN
    )
    data object Fault : DisplayState(
        R.id.warnBtn, DisplayType.DISPLAY_TYPE_FAULT, SettingsConfig.ACTION_FAULT_FRAGMENT_OPEN
    )
    data object Info : DisplayState(
        R.id.quesBtn, DisplayType.DISPLAY_TYPE_INFO, SettingsConfig.ACTION_HELP_FRAGMENT_OPEN
    )

    companion object {
        fun fromTypeId(viewId: Int): DisplayState {
            return when (viewId) {
                DisplayType.DISPLAY_TYPE_HOME -> Home
                DisplayType.DISPLAY_TYPE_SETTING -> Setting
                DisplayType.DISPLAY_TYPE_HVAC -> Hvac
                DisplayType.DISPLAY_TYPE_FAULT -> Fault
                DisplayType.DISPLAY_TYPE_INFO -> Info
                else -> Unknown
            }
        }
    }
}
