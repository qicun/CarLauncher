/*
 * Copyright (c) <2025>, <zlingsmart technology Corporation>.
 * All rights reserved.
 * The use, copying, distribution, transmission and modification of this file
 * are allowed only with the written authorization of Zlingsmart Technology.
 */

package com.raite.crcc.systemui.config

/**
 * @Author zl
 * @Date 2023/9/1
 * @Description
 * Launcher相关配置
 */
object LauncherConfig {
    @Deprecated("")
    const val LAUNCHER_INTENT_DISPLAY = "INTENT_DISPLAY"
    const val LAUNCHER_ALL_MENU = "com.android.raite.systemui.allmenu"
    const val LAUNCHER_PACKAGE_NAME = "com.raite.launcher"
    const val LAUNCHER_SHOW_APPS_HOST = "Launcher_Popup_Window_Main"
    const val LAUNCHER_SHOW_APPS_SUB = "Launcher_Popup_Window_Second"
    const val LAUNCHER_MAP_PACKAGE_NAME = "com.raite.launcher"
    const val LAUNCHER_MAP_CLASS_NAME = "com.raite.raiteamapnavi.activity.RouteActivity"
    const val LAUNCHER_NAVI_MAP_CLASS_NAME = "com.raite.raiteamapnavi.activity.ShowNaviActivity"
    const val LAUNCHER_MAP_SERVICE_CLASS_NAME = "com.raite.raiteamapnavi.service.NaviService"
    const val UPDATE_TYPE_MAP_STATE = 15
}