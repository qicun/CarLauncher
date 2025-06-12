/*
 * Copyright (c) <2025>, <zlingsmart technology Corporation>.
 * All rights reserved.
 * The use, copying, distribution, transmission and modification of this file
 * are allowed only with the written authorization of Zlingsmart Technology.
 */

package com.raite.crcc.systemui.config

/**
 * @Author zl
 * @Date 2023/12/18
 * @Description
 * 系统设置相关
 */
object SettingsConfig {
    // package name
    const val SETTINGS_PACKAGE_NAME = "com.raite.crcc.controller"

    // class name
    const val SETTINGS_CLASS_NAME = "com.raite.crcc.controller.ui.SystemActivity"

    // class name
    const val SETTINGS_HELP_ACTIVITY_CLASS_NAME = "com.raite.crcc.controller.ui.help.HelpActivity"

    private const val PREFIX = "controller"

    /**
     * Key值
     */
    const val KEY_ACTION = "${PREFIX}_key_action"

    /**
     * 打开设置页
     */
    const val ACTION_SETTING_FRAGMENT_OPEN = "${PREFIX}_action_system_fragment_setting_open"

    /**
     * 打开系统页
     */
    const val ACTION_SYSTEM_FRAGMENT_OPEN = "${PREFIX}_action_system_fragment_open"

    /**
     * 打开系统子页
     */
    const val ACTION_SYSTEM_FRAGMENT_BRAKE_OPEN = "${PREFIX}_action_system_fragment_brake_open"
    const val ACTION_SYSTEM_FRAGMENT_DRAG_OPEN = "${PREFIX}_action_system_fragment_drag_open"
    const val ACTION_SYSTEM_FRAGMENT_TRACK_OPEN = "${PREFIX}_action_system_fragment_track_open"
    const val ACTION_SYSTEM_FRAGMENT_PIS_OPEN = "${PREFIX}_action_system_fragment_pis_open"
    const val ACTION_SYSTEM_FRAGMENT_DEVICE_OPEN = "${PREFIX}_action_system_fragment_device_open"
    const val ACTION_SYSTEM_FRAGMENT_HVAC_OPEN = "${PREFIX}_action_system_fragment_hvac_open"

    /**
     * 打开故障页
     */
    const val ACTION_FAULT_FRAGMENT_OPEN = "${PREFIX}_action_system_fragment_fault_open"

    /**
     * 打开帮助页
     */
    const val ACTION_HELP_FRAGMENT_OPEN = "${PREFIX}_action_help_fragment_open"

    /**
     * 打开帮助子页
     */
    const val ACTION_HELP_FRAGMENT_RUNNING_STATE_OPEN =
        "${PREFIX}_action_help_fragment_running_state_open"
    const val ACTION_HELP_FRAGMENT_BRAKE_OPEN = "${PREFIX}_action_help_fragment_brake_open"
    const val ACTION_HELP_FRAGMENT_TRACK_OPEN = "${PREFIX}_action_help_fragment_track_open"

    /**
     * 打开工程模式页
     */
    const val ACTION_ENGINE_MODE_ACTIVITY_OPEN = "${PREFIX}_action_engine_mode_activity_open"
}