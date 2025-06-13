/*
 * Copyright (c) <2025>, <zlingsmart technology Corporation>.
 * All rights reserved.
 * The use, copying, distribution, transmission and modification of this file
 * are allowed only with the written authorization of Zlingsmart Technology.
 */

package com.raite.crcc.systemui.utils

import android.annotation.ArrayRes
import android.annotation.ColorRes
import android.annotation.DisplayContext
import android.annotation.DrawableRes
import android.annotation.StringRes
import android.annotation.UiContext
import android.content.Context
import android.graphics.drawable.Drawable
import androidx.annotation.DimenRes
import androidx.core.content.ContextCompat
import com.raite.crcc.systemui.util.ContextUtil

/**
 * @Author zl
 * @Date 2023/8/22
 * @Description
 */

fun getString(@StringRes res: Int): String {
    return ContextUtil.context.resources.getString(res)
}

fun getString(@StringRes res: Int, content: String): String {
    return ContextUtil.context.resources.getString(res, content)
}

@UiContext
fun Context.getRaiteString(@StringRes res: Int): String {
    return this.resources.getString(res)
}

fun getRaiteStringArray(@ArrayRes id: Int): Array<String> {
    return ContextUtil.context.resources.getStringArray(id)
}

@UiContext
fun Context.getStringArray(@ArrayRes id: Int): Array<String> {
    return this.resources.getStringArray(id)
}

@DisplayContext
fun Context.getRaiteColor(@ColorRes id: Int): Int {
    return ContextCompat.getColor(this, id)
}

@DisplayContext
fun Context.getRaiteDrawable(@DrawableRes id: Int): Drawable? {
    return ContextCompat.getDrawable(this, id)
}

fun getColor(@ColorRes id: Int): Int {
    return ContextUtil.context.getColor(id)
}

fun getStringArray(@ArrayRes id: Int): Array<String> {
    return ContextUtil.context.resources.getStringArray(id)
}

fun getDrawable(@DrawableRes id: Int): Drawable? {
    return ContextCompat.getDrawable(ContextUtil.context, id)
}

fun getDimen(@DimenRes id: Int): Float {
    return ContextUtil.context.resources.getDimension(id)
}