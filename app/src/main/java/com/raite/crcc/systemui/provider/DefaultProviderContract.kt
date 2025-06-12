/*
 * Copyright (c) <2025>, <zlingsmart technology Corporation>.
 * All rights reserved.
 * The use, copying, distribution, transmission and modification of this file
 * are allowed only with the written authorization of Zlingsmart Technology.
 */

package com.raite.crcc.systemui.provider

import android.net.Uri
import androidx.core.net.toUri


/**
 * @Author zl
 * @Date 2023/12/18
 * @Description
 */
object DefaultProviderContract {

    const val AUTHORITY: String = "com.raite.crcc.systemui.defaultprovider"
    var CONTENT_URI: Uri = ("content://$AUTHORITY").toUri()

    // 监听变化uri，获取key对应的变化uri
    fun getProviderChangeUri(key: String): Uri = Uri.withAppendedPath(CONTENT_URI, key)

    const val DEFAULT_SP_NAME: String = "rail_systemui"

    // 主屏显示类型
    const val KEY_DISPLAY_TYPE: String = "display_type"

    // 副屏显示类似
    const val KEY_SUB_DISPLAY_TYPE: String = "sub_display_type"

    // 空调-选中车厢类型
    const val KEY_CARRIAGE_TYPE: String = "carriage_type"

    // 控制屏选中的车厢头信息
    const val KEY_CRCC_CARRIAGE_HEAD_TYPE: String = "crcc_carriage_head_type"

    // 更新全局焦点 indicator
    const val UPDATE_GLOBAL_FOCUS_INDICATOR: String = "update_global_focus_indicator"

    // 更新全局按键
    const val UPDATE_GLOBAL_FOCUS_KEY_CODE: String = "update_global_focus_key_code"

    const val NVM_LOG_TABLE_NAME: String = "nvm_log"
    const val NVM_LOG_TABLE_CODE: Int = 1
    const val NVM_LOG_TABLE_COLUMNS_TIME: String = "time"
    const val NVM_LOG_TABLE_COLUMNS_TYPE: String = "type"
    const val NVM_LOG_TABLE_COLUMNS_ACTION: String = "action"
    const val NVM_LOG_TABLE_COLUMNS_DOMAIN: String = "domain"
    const val NVM_LOG_TABLE_COLUMNS_REASON: String = "reason"
    const val NVM_LOG_TABLE_OPTION_ALL: String = "all"
    const val NVM_LOG_TABLE_OPTION_SEARCH: String = "search"
    const val NVM_LOG_TABLE_OPTION_LATEST: String = "latest"
    const val NVM_LOG_TABLE_LOG_MAX: Int = 2000
    const val NVM_LOG_TABLE_LOG_DELETE: Int = 500


    fun getAllIntKey() = listOf(
        KEY_DISPLAY_TYPE,
        KEY_SUB_DISPLAY_TYPE,
        KEY_CARRIAGE_TYPE,
        KEY_CRCC_CARRIAGE_HEAD_TYPE,
    )

    fun getGetMethod(key: String): String = "get_${key}"

    fun getSetMethod(key: String): String = "set_${key}"
}