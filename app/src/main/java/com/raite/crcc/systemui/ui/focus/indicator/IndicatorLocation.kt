/*
 * Copyright (c) <2025>, <zlingsmart technology Corporation>.
 * All rights reserved.
 * The use, copying, distribution, transmission and modification of this file
 * are allowed only with the written authorization of Zlingsmart Technology.
 */

package com.raite.crcc.systemui.ui.focus.indicator

data class IndicatorLocation(
    val x: Float = 0f,
    val y: Float = 0f,
    val width: Float = 0f,
    val height: Float = 0f,
)

fun IndicatorLocation.padding(padding: Float = 0f): IndicatorLocation {
    return IndicatorLocation(
        x = this.x - padding,
        y = this.y - padding,
        width = this.width + padding + padding,
        height = this.height + padding + padding,
    )
}