package com.raite.crcc.systemui.common

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface NvmLogDao {
    /**
     * 添加1条日志
     */
    @Insert
    fun insertLog(log: NvmLog)

    /**
     * 获取所以日志
     */
    @Query("SELECT * from nvm_log")
    fun getAllLog():Cursor

    /**
     * 搜索时间段内的日志
     */
    @Query("SELECT * from nvm_log where type >= :type and time >= :start and time <= :end")
    fun getLogDuring(type: Int, start: Long, end: Long):Cursor

    /**
     * 获取日志总条数
     */
    @Query("SELECT count(*) from nvm_log")
    fun getLogCount():Int

    /**
     * 删除日志
     */
    @Query("DELETE from nvm_log where id in (:ids)")
    fun deleteLogs(ids: List<Int>)

    /**
     * 获取时间最久的count条日志
     */
    @Query("SELECT id from nvm_log ORDER BY id ASC LIMIT :count")
    fun getOldestLog(count: Int):List<Int>

    /**
     * 获取最新的count条日志
     */
    @Query("SELECT * from nvm_log ORDER BY id ASC LIMIT :count")
    fun getLatestLog(count: Int):Cursor
}