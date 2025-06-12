package com.raite.crcc.systemui.common

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [NvmLog::class], version = 1)
abstract class NvmLogDatabase: RoomDatabase() {
    abstract fun nvmLogDao(): NvmLogDao
}