/*
 * Copyright (c) <2025>, <zlingsmart technology Corporation>.
 * All rights reserved.
 * The use, copying, distribution, transmission and modification of this file
 * are allowed only with the written authorization of Zlingsmart Technology.
 */

package com.raite.crcc.systemui.config

/**
 * @Author lsh
 * @Date 2023/6/30 14:11
 * @Description
 */
object DisplayType {
    /** 默认值 */
    const val DISPLAY_TYPE_NONE = 0

    /** 控制屏 -主屏- system ui */
    const val DISPLAY_TYPE_STATUS_BAR = 11
    const val DISPLAY_TYPE_NAVIGATION_BAR = 12
    /** 监控屏 -副屏- system ui */
    const val SUB_DISPLAY_TYPE_STATUS_BAR = 21
    const val SUB_DISPLAY_TYPE_NAVIGATION_BAR = 22

    /** 控制屏 - 其他页面（不在底部高亮图片显示页） */
    const val DISPLAY_TYPE_OTHER = 100

    /** 控制屏 -首页 */
    const val DISPLAY_TYPE_HOME = 101

    /** 控制屏 -设置 */
    const val DISPLAY_TYPE_SETTING = 102

    /** 控制屏 -空调 */
    const val DISPLAY_TYPE_HVAC = 103

    /** 控制屏 -故障 */
    const val DISPLAY_TYPE_FAULT = 104

    /** 控制屏 -信息 */
    const val DISPLAY_TYPE_INFO = 105

    /** 控制屏 -系统- 导航栏没对应按钮 */
    const val DISPLAY_TYPE_SYSTEM = 106

    /** 监控屏 - 其他页面（不在底部高亮图片显示页） */
    const val SUB_DISPLAY_TYPE_OTHER  = 200
    /** 监控屏 -监控 */
    const val SUB_DISPLAY_TYPE_CAMERA = 201

    /** 监控屏 -360 */
    const val SUB_DISPLAY_TYPE_A360 = 202

    /** 监控屏 -视频 */
    const val SUB_DISPLAY_TYPE_VIDEO = 203

    /** 监控屏 -硬盘 */
    const val SUB_DISPLAY_TYPE_DISK = 204

    /** 监控屏 -日志 */
    const val SUB_DISPLAY_TYPE_LOG= 205

    /** 监控屏 -登录 */
    const val SUB_DISPLAY_TYPE_LOGIN= 206

    /** 监控屏 - 用户管理 */
    const val SUB_DISPLAY_TYPE_USE_MANAGER= 207

}