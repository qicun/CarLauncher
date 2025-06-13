/*
 * Copyright (c) <2025>, <zlingsmart technology Corporation>.
 * All rights reserved.
 * The use, copying, distribution, transmission and modification of this file
 * are allowed only with the written authorization of Zlingsmart Technology.
 */

package com.raite.crcc.systemui.utils

import android.util.Log
import com.raite.crcc.systemui.BuildConfig

/**
 * @Author zl
 * @Date 2023/8/29
 * @Description 日志
 */

internal object Plog {
    const val TAG = "systemui"
    fun i(tag: String, msg: String) {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "$tag $msg")
        }
    }

    fun d(tag: String, msg: String) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "$tag $msg")
        }
    }

    fun v(tag: String, msg: String) {
        if (BuildConfig.DEBUG) {
            Log.v(TAG, "$tag $msg")
        }
    }

    fun w(tag: String, msg: String) {
        Log.w(TAG, "$tag $msg")
    }

    fun w(tag: String, msg: String, e: Throwable) {
        Log.w(TAG, "$tag $msg", e)
    }

    fun e(tag: String, msg: String) {
        Log.e(TAG, "$tag $msg")
    }

    fun e(tag: String, msg: String, e: Throwable) {
        Log.e(TAG, "$tag $msg", e)
    }
}

fun Any.logi(msg: String) {
    if (BuildConfig.DEBUG) {
        Plog.i(this::class.java.simpleName, msg)
    }
}

fun Any.logw(msg: String) {
    Plog.w(this::class.java.simpleName, msg)
}

fun Any.loge(msg: String) {
    Plog.e(this::class.java.simpleName, msg)
}