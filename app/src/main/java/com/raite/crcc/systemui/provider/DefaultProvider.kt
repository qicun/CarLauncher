/*
 * Copyright (c) <2025>, <zlingsmart technology Corporation>.
 * All rights reserved.
 * The use, copying, distribution, transmission and modification of this file
 * are allowed only with the written authorization of Zlingsmart Technology.
 */
package com.raite.crcc.systemui.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.core.content.edit
import androidx.room.Room
import com.raite.crcc.systemui.common.NvmLog
import com.raite.crcc.systemui.common.NvmLogDao
import com.raite.crcc.systemui.common.NvmLogDatabase
import com.raite.crcc.systemui.provider.DefaultProviderContract.UPDATE_GLOBAL_FOCUS_INDICATOR
import com.raite.crcc.systemui.provider.DefaultProviderContract.UPDATE_GLOBAL_FOCUS_KEY_CODE
import com.raite.crcc.systemui.ui.focus.indicator.GlobalDisplayFocusIndicatorManager
import com.raite.crcc.systemui.utils.Plog


class DefaultProvider : ContentProvider() {
    private val TAG = "DefaultProvider"
    private lateinit var mSharedPreferences: SharedPreferences
    private lateinit var mNvmLogDbInstance: NvmLogDatabase
    private lateinit var mNvmLogDbDao: NvmLogDao
    private val mMatcher: UriMatcher = UriMatcher(UriMatcher.NO_MATCH)

    override fun onCreate(): Boolean {
        Plog.i(TAG, "onCreate")
        context?.let {
            mSharedPreferences = it.getSharedPreferences(
                DefaultProviderContract.DEFAULT_SP_NAME, Context.MODE_PRIVATE
            )
            mNvmLogDbInstance = Room.databaseBuilder(
                it,
                NvmLogDatabase::class.java,
                "nvmlog_database").build()
            mNvmLogDbDao = mNvmLogDbInstance.nvmLogDao()
            mMatcher.addURI(DefaultProviderContract.AUTHORITY,
                DefaultProviderContract.NVM_LOG_TABLE_NAME,
                DefaultProviderContract.NVM_LOG_TABLE_CODE)
        }
        return true
    }

    override fun call(method: String, arg: String?, extras: Bundle?): Bundle? {
        val cBundle = Bundle()

        if (method == UPDATE_GLOBAL_FOCUS_INDICATOR) {
            // 更新全局焦点 indicator
            GlobalDisplayFocusIndicatorManager.updateIndicator(extras)
            return cBundle
        }

        if (method == UPDATE_GLOBAL_FOCUS_KEY_CODE) {
            // 更新应用按键
            GlobalDisplayFocusIndicatorManager.updateAppKeyCode(extras)
            return cBundle
        }

        DefaultProviderContract.getAllIntKey().forEach { key ->
            if (method == DefaultProviderContract.getGetMethod(key)) {
                getSpKey(cBundle, key)
            } else if (method == DefaultProviderContract.getSetMethod(key)) {
                setSpKey(extras, key)
            }
        }
        return cBundle
    }

    private fun getSpKey(bun: Bundle, key: String) {
        bun.putInt(key, mSharedPreferences.getInt(key, 0))
    }

    private fun setSpKey(bun: Bundle?, key: String) {
        val type = bun?.getInt(key) ?: 0
        mSharedPreferences.edit { putInt(key, type) }
        context?.contentResolver?.notifyChange(
            DefaultProviderContract.getProviderChangeUri(key), null
        )
    }

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? {
        val table = getTableName(uri)

        when(table) {
            DefaultProviderContract.NVM_LOG_TABLE_NAME -> {
                if(selection?.equals(DefaultProviderContract.NVM_LOG_TABLE_OPTION_ALL) == true) {
                    return mNvmLogDbDao.getAllLog()
                } else if((selection?.equals(
                        DefaultProviderContract.NVM_LOG_TABLE_OPTION_SEARCH) == true)
                    && (selectionArgs?.size == 3)) {

                    val type = selectionArgs[0].toInt()
                    val start = selectionArgs[1].toLong()
                    val end = selectionArgs[2].toLong()

                    return mNvmLogDbDao.getLogDuring(type ,start, end)
                } else if(
                    ((selection?.equals(
                        DefaultProviderContract.NVM_LOG_TABLE_OPTION_LATEST)) == true)
                    && (selectionArgs?.size == 1)) {

                    val count = selectionArgs[0].toInt()

                    return mNvmLogDbDao.getLatestLog(count)
                }
            }

            else -> {}
        }
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri {
        val table = getTableName(uri)

        when(table) {
            DefaultProviderContract.NVM_LOG_TABLE_NAME -> {

                val type =
                    values?.getAsInteger(DefaultProviderContract.NVM_LOG_TABLE_COLUMNS_TYPE)
                val time =
                    values?.getAsLong(DefaultProviderContract.NVM_LOG_TABLE_COLUMNS_TIME)
                val action =
                    values?.getAsString(DefaultProviderContract.NVM_LOG_TABLE_COLUMNS_ACTION)
                val reason =
                    values?.getAsString(DefaultProviderContract.NVM_LOG_TABLE_COLUMNS_REASON)
                val domain =
                    values?.getAsString(DefaultProviderContract.NVM_LOG_TABLE_COLUMNS_DOMAIN)

                if(type != null && time !== null && action != null) {
                    mNvmLogDbDao.insertLog(
                        NvmLog(
                            time = time,
                            type = type,
                            action = action,
                            domain = domain,
                            reason = reason))
                }

                // 当日志条目数超过NVM_LOG_TABLE_LOG_MAX，删除最早的NVM_LOG_TABLE_LOG_DELETE条
                // 避免低效的逐条删除操作
                if(mNvmLogDbDao.getLogCount() > DefaultProviderContract.NVM_LOG_TABLE_LOG_MAX) {
                    mNvmLogDbDao.deleteLogs(
                        mNvmLogDbDao.getOldestLog(DefaultProviderContract.NVM_LOG_TABLE_LOG_DELETE))
                }
            }

            else -> {}
        }

        return uri
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int = 0

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int = 0

    override fun getType(uri: Uri): String? = null

    private fun getTableName(uri: Uri): String? {
        return when(mMatcher.match(uri)) {
            1 -> DefaultProviderContract.NVM_LOG_TABLE_NAME
            else -> null
        }
    }
}