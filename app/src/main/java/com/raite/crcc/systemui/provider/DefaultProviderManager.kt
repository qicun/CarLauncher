/*
 * Copyright (c) <2025>, <zlingsmart technology Corporation>.
 * All rights reserved.
 * The use, copying, distribution, transmission and modification of this file
 * are allowed only with the written authorization of Zlingsmart Technology.
 */

package com.raite.crcc.systemui.provider

import android.content.Context
import android.os.Bundle
import com.raite.crcc.systemui.provider.DefaultProviderContract.KEY_CARRIAGE_TYPE
import com.raite.crcc.systemui.provider.DefaultProviderContract.KEY_DISPLAY_TYPE
import com.raite.crcc.systemui.provider.DefaultProviderContract.KEY_SUB_DISPLAY_TYPE
import com.raite.crcc.systemui.utils.Plog


/**
 * @Author lsh
 * @Date 2023/7/12 11:23
 * @Description
 */
object DefaultProviderManager {

    const val TAG = "DefaultProviderManager"

    /////////////////////////////////////////////////////////////
    //主屏显示类型
    fun setDisplayType(context: Context, type: Int) {
        setDisplayTypeInt(context, KEY_DISPLAY_TYPE, type)
    }

    fun getDisplayType(context: Context): Int = getDisplayTypeInt(context, KEY_DISPLAY_TYPE)

    /////////////////////////////////////////////////////////////
    //副屏显示类型
    fun setSubDisplayType(context: Context, type: Int) {
        setDisplayTypeInt(context, KEY_SUB_DISPLAY_TYPE, type)
    }

    fun getSubDisplayType(context: Context): Int = getDisplayTypeInt(context, KEY_SUB_DISPLAY_TYPE)

    /////////////////////////////////////////////////////////////
    //车厢类型
    fun setCarriageType(context: Context, type: Int) {
        setDisplayTypeInt(context, KEY_CARRIAGE_TYPE, type)
    }

    fun getCarriageType(context: Context) = getDisplayTypeInt(context, KEY_CARRIAGE_TYPE)

    /////////////////////////////////////////////////////////////

    private fun setDisplayTypeInt(
        context: Context, key: String, value: Int
    ) {
        Plog.i(TAG, "setDisplayTypeInt key:$key value$value")
        val bundle = Bundle()
        bundle.putInt(key, value)
        context.contentResolver.call(
            DefaultProviderContract.CONTENT_URI,
            DefaultProviderContract.getSetMethod(key),
            null,
            bundle
        )
    }

    private fun getDisplayTypeInt(context: Context, key: String): Int {
        val resultBundle = context.contentResolver.call(
            DefaultProviderContract.CONTENT_URI,
            DefaultProviderContract.getGetMethod(key),
            null,
            null
        )
        Plog.i(TAG, "getDisplayTypeInt key:$key resultBundle$resultBundle")
        return resultBundle?.getInt(key) ?: 0
    }
}