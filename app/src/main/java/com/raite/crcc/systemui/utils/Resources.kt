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
import androidx.core.content.ContextCompat
import com.raite.crcc.systemui.App

/**
 * @Author zl
 * @Date 2023/8/22
 * @Description
 */

fun getString(@StringRes res: Int): String {
    return App.mContext.resources.getString(res)
}

fun getString(@StringRes res: Int, content: String): String {
    return App.mContext.resources.getString(res, content)
}

@UiContext
fun Context.getRaiteString(@StringRes res: Int): String {
    return this.resources.getString(res)
}

fun getRaiteStringArray(@ArrayRes id: Int): Array<String> {
    return App.mContext.resources.getStringArray(id)
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