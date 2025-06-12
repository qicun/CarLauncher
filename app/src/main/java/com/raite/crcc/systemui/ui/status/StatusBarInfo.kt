/*
 * Copyright (c) <2025>, <zlingsmart technology Corporation>.
 * All rights reserved.
 * The use, copying, distribution, transmission and modification of this file
 * are allowed only with the written authorization of Zlingsmart Technology.
 */

package com.raite.crcc.systemui.ui.status

import com.raite.crcc.systemui.config.UpdateType.UPDATE_TYPE_NONE

/**
 * @Author lsh
 * @Date 2023/6/30 14:00
 * @Description
 * mUpdateType 更新类型
 * resId 跟新的资源ID
 * terminalStationName 终点站名称
 * currentStationName 当前站名称
 * nextStationName 下一站名称
 */
data class StatusBarInfo(
    var updateType: Int = UPDATE_TYPE_NONE,
    var resId: Int = 0,
    var time: String = "00:00",
    var value1: Boolean = false,
    var value2: Int = 0,
    var value3: Int = 0,
    var value4: Int = 0,
    var value5: String = "",
    var value6: String = ""
)