/*
 * Copyright (c) <2025>, <zlingsmart technology Corporation>.
 * All rights reserved.
 * The use, copying, distribution, transmission and modification of this file
 * are allowed only with the written authorization of Zlingsmart Technology.
 */

package com.raite.crcc.systemui.config

/**
 * @Author lsh
 * @Date 2023/8/5 10:00
 * @Description
 */
object WifiSignalLevel {
    const val LEVEL_4 = 4
    const val LEVEL_3 = 3
    const val LEVEL_2 = 2
    const val LEVEL_1 = 1
    const val LEVEL_0 = 0

    private const val LEVEL_TOP = -60
    private const val LEVEL_MIDDLE = -80
    private const val LEVEL_LM = -100
    private const val LEVEL_LOW = -200

    fun getLevel(rssi: Int): Int {
        return if (rssi >= LEVEL_TOP) {
            LEVEL_4
        } else if (rssi >= LEVEL_MIDDLE) {
            LEVEL_3
        } else if (rssi >= LEVEL_LM) {
            LEVEL_2
        } else if (rssi >= LEVEL_LOW) {
            LEVEL_1
        } else {
            LEVEL_0
        }
    }
}