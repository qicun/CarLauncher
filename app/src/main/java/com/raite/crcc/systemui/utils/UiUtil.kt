/*
 * Copyright (c) <2025>, <zlingsmart technology Corporation>.
 * All rights reserved.
 * The use, copying, distribution, transmission and modification of this file
 * are allowed only with the written authorization of Zlingsmart Technology.
 */

package com.raite.crcc.systemui.utils

import android.annotation.ColorRes
import android.annotation.DisplayContext
import android.annotation.DrawableRes
import android.bluetooth.BluetoothHeadsetClientCall
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.raite.crcc.systemui.App
import com.raite.crcc.systemui.R
import com.raite.crcc.systemui.config.WifiSignalLevel

/**
 * @Author lsh
 * @Date 2023/7/25 14:35
 * @Description
 */
object UiUtil {
    fun ImageButton.setRaiteDrawable(@DisplayContext context: Context, @DrawableRes id: Int) {
        this.setImageDrawable(context.getRaiteDrawable(id))
    }

    fun ImageView.setRaiteDrawable(@DisplayContext context: Context, @DrawableRes id: Int) {
        this.setImageDrawable(context.getRaiteDrawable(id))
    }

    fun TextView.setRaiteTextColor(@DisplayContext context: Context, @ColorRes id: Int) {
        this.setTextColor(context.getRaiteColor(id))
    }

    fun View.setRaiteBackgroundColor(@DisplayContext context: Context, @ColorRes id: Int) {
        this.setBackgroundColor(context.getRaiteColor(id))
    }

    fun View.setRaiteBackground(@DisplayContext context: Context, @DrawableRes id: Int) {
        this.background = context.getRaiteDrawable(id)
    }
}