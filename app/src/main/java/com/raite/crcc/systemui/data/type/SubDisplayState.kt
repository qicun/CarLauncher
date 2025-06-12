/*
 * Copyright (c) <2025>, <zlingsmart technology Corporation>.
 * All rights reserved.
 * The use, copying, distribution, transmission and modification of this file
 * are allowed only with the written authorization of Zlingsmart Technology.
 */

package com.raite.crcc.systemui.data.type

import com.raite.crcc.systemui.R
import com.raite.crcc.systemui.config.CctvConfig
import com.raite.crcc.systemui.config.DisplayType

/**
 * 副屏显示类
 */
sealed class SubDisplayState(val viewId: Int = 0, val typeId :Int, val className: String = "") {

    data object Unknown : SubDisplayState(typeId =DisplayType.DISPLAY_TYPE_HOME)
    data object Camera : SubDisplayState(
        R.id.cameraBtn, DisplayType.SUB_DISPLAY_TYPE_CAMERA, CctvConfig.CCTV_CAMERA_CLASS_NAME
    )
    data object A360 : SubDisplayState(
        R.id.a360Btn, DisplayType.SUB_DISPLAY_TYPE_A360, CctvConfig.CCTV_360_CLASS_NAME
    )
    data object Video : SubDisplayState(
        R.id.videoBtn, DisplayType.SUB_DISPLAY_TYPE_VIDEO,  CctvConfig.CCTV_VIDEO_CLASS_NAME
    )
    data object Disk : SubDisplayState(
        R.id.diskBtn, DisplayType.SUB_DISPLAY_TYPE_DISK, CctvConfig.CCTV_DISK_CLASS_NAME
    )
    data object Log : SubDisplayState(
        R.id.logBtn, DisplayType.SUB_DISPLAY_TYPE_LOG, CctvConfig.CCTV_LOG_CLASS_NAME
    )

    companion object {
        fun fromViewId(viewId: Int): SubDisplayState {
            return when (viewId) {
                R.id.cameraBtn -> Camera
                R.id.a360Btn -> A360
                R.id.videoBtn -> Video
                R.id.diskBtn -> Disk
                R.id.logBtn -> Log
                else -> Unknown
            }
        }

        fun fromTypeId(viewId: Int): SubDisplayState {
            return when (viewId) {
                DisplayType.SUB_DISPLAY_TYPE_CAMERA -> Camera
                DisplayType.SUB_DISPLAY_TYPE_A360 -> A360
                DisplayType.SUB_DISPLAY_TYPE_VIDEO -> Video
                DisplayType.SUB_DISPLAY_TYPE_DISK -> Disk
                DisplayType.SUB_DISPLAY_TYPE_LOG -> Log
                else -> Unknown
            }
        }
    }
}
