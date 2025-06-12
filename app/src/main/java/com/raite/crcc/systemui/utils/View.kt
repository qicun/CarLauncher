/*
 * Copyright (c) <2025>, <zlingsmart technology Corporation>.
 * All rights reserved.
 * The use, copying, distribution, transmission and modification of this file
 * are allowed only with the written authorization of Zlingsmart Technology.
 */

package com.raite.crcc.systemui.utils

import android.annotation.SuppressLint
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.view.View
import androidx.core.graphics.toColorInt

/**
 * @Author zl
 * @Date 2023/12/5
 * @Description
 * 控件置灰
 */

private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
private val cm = ColorMatrix(
    floatArrayOf(
        1f, 0f, 0f, 0f, 0f,
        0f, 1f, 0f, 0f, 0f,
        0f, 0f, 1f, 0f, 0f,
        0f, 0f, 0f, 1f, 0f
    )
)

fun View.setGreyView(isGrey: Boolean) {
    if (isGrey) {
        cm.setSaturation(0f)
        paint.color = "#99000000".toColorInt()
    } else {
        cm.setSaturation(1f)
        paint.color = "#FFFFFFFF".toColorInt()
    }
    paint.colorFilter = ColorMatrixColorFilter(cm)
    this.setLayerType(View.LAYER_TYPE_HARDWARE, paint)
}

fun View.setRaiteOnLongClickListener(click: Block) {
    this.setOnLongClickListener { longClick { click.invoke() } }
}

private inline fun longClick(click: Block): Boolean {
    click.invoke()
    return true
}

@SuppressLint("ClickableViewAccessibility")
fun View.setRaiteOnTouchListener(click: Block) {
    this.setOnTouchListener { v, event ->
        click.invoke()
        false
    }
}

internal inline fun <reified T : View> T.isVisible() = this.visibility == View.VISIBLE

internal inline fun <reified T : View> T.visible() {
    this.visibility = View.VISIBLE
}

internal inline fun <reified T : View> T.invisible() {
    this.visibility = View.INVISIBLE
}

internal inline fun <reified T : View> T.gone() {
    clearAnimation()
    this.visibility = View.GONE
}