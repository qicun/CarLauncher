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
 * 副屏-监控屏相关
 */
object CctvConfig {

    // package name
    const val CCTV_PACKAGE_NAME = "com.raite.crcc.cctv"
    // class name
    const val CCTV_CAMERA_CLASS_NAME = "com.raite.crcc.cctv.ui.main.MainActivity"
    const val CCTV_360_CLASS_NAME = "com.raite.crcc.cctv.ui.panorama.PanoramaActivity"
    const val CCTV_VIDEO_CLASS_NAME = "com.raite.crcc.cctv.ui.video.VideoActivity"
    const val CCTV_DISK_CLASS_NAME = "com.raite.crcc.cctv.ui.device.DeviceActivity"
    const val CCTV_LOG_CLASS_NAME = "com.raite.crcc.cctv.ui.log.LogActivity"

    // service class name
    const val CCTV_SERVICE_CLASS_NAME = "com.raite.crcc.cctv.service.CctvHmiService"
    const val CCTV_KEY_USE_LOGIN_TYPE = "type"
    const val CCTV_VALUE_USE_LOGIN_TYPE = "CCTV_USER_INFO"
}