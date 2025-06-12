/*
 * Copyright (c) <2025>, <zlingsmart technology Corporation>.
 * All rights reserved.
 * The use, copying, distribution, transmission and modification of this file
 * are allowed only with the written authorization of Zlingsmart Technology.
 */

package com.raite.crcc.systemui.data.type

/**
 * 空调模式
 * @param tempMin 最低温度
 * @param tempMax 最高温度
 */
sealed class HvacCarriageMode(
    val tempMin: Float = Float.MIN_VALUE,
    val tempMax: Float = Float.MAX_VALUE,
) {

    object Unknown : HvacCarriageMode()

    object Invalid : HvacCarriageMode()

    /**
     * 通风
     */
    object Wind : HvacCarriageMode()

    /**
     * 制冷
     */
    object Cold : HvacCarriageMode(22F, 28F)

    /**
     * 制热
     */
    object Warm : HvacCarriageMode(14F, 20F)

    /**
     * 停止
     */
    object Stop : HvacCarriageMode()

    /**
     * 强制制冷
     */
    object ForceCold : HvacCarriageMode(22F, 28F)

    /**
     * 强制制热
     */
    object ForceWarm : HvacCarriageMode(14F, 20F)

    /**
     * 自动
     */
    object Auto : HvacCarriageMode()
}

/**
 * 使温度在范围内
 */
fun HvacCarriageMode?.fixTemperature(temp: Float): Float {
    if (this == null) return temp
    if (temp < tempMin) {
        return tempMin
    }
    if (temp > tempMax) {
        return tempMax
    }
    return temp
}