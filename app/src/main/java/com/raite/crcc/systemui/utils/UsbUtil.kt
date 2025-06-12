/*
 * Copyright (c) <2025>, <zlingsmart technology Corporation>.
 * All rights reserved.
 * The use, copying, distribution, transmission and modification of this file
 * are allowed only with the written authorization of Zlingsmart Technology.
 */

package com.raite.crcc.systemui.utils

import android.os.storage.StorageManager
import android.os.storage.StorageVolume
import android.text.TextUtils
import java.lang.reflect.Method

/**
 * @Author lsh
 * @Date 2023/7/31 11:30
 * @Description
 */
object UsbUtil {

    /**
     * 获取挂载设备列表
     * 最少会返回一条内部存储目录
     */
    fun getVolumeList(storageManager: StorageManager): Array<StorageVolume> {
        try {
            val clz: Class<*> = StorageManager::class.java
            val getVolumeList: Method = clz.getMethod("getVolumeList")
            return getVolumeList.invoke(storageManager) as Array<StorageVolume>
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return emptyArray()
    }

    /**
     * Gets the state of a volume via its mountpoint.
     * if (USBUtil.getVolumeState(storageManager, usbPath)
     *          .equals(android.os.Environment.MEDIA_MOUNTED)){
     *      return true;
     * }
     */
    fun getVolumeState(storageManager: StorageManager?, path: String?): String? {
        var result = ""
        if (null == storageManager || TextUtils.isEmpty(path)) {
            return result
        }
        try {
            val clz: Class<*> = StorageManager::class.java
            val getVolumeList = clz.getMethod("getVolumeState", String::class.java)
            result = getVolumeList.invoke(storageManager, path) as String
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return result
    }
}